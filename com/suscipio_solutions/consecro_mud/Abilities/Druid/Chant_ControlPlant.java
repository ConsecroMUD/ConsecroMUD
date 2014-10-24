package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;




@SuppressWarnings("rawtypes")
public class Chant_ControlPlant extends Chant
{
	@Override public String ID() { return "Chant_ControlPlant"; }
	private final static String localizedName = CMLib.lang().L("Control Plant");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTCONTROL;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}

	public static Ability isPlant(Item I)
	{
		if((I!=null)&&(I.rawSecretIdentity().length()>0))
		{
			for(final Enumeration<Ability> a=I.effects();a.hasMoreElements();)
			{
				final Ability A=a.nextElement();
				if((A!=null)
				&&(A.invoker()!=null)
				&&(A instanceof Chant_SummonPlants))
					return A;
			}
		}
		return null;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Item myPlant=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_ANY);
		if(myPlant==null) return false;

		if(isPlant(myPlant)==null)
		{
			mob.tell(L("You can't control @x1.",myPlant.name()));
			return false;
		}

		if(myPlant.rawSecretIdentity().equals(mob.Name()))
		{
			mob.tell(L("You already control @x1.",myPlant.name()));
			return false;
		}


		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,myPlant,this,verbalCastCode(mob,myPlant,auto),auto?"":L("^S<S-NAME> chant(s) to <T-NAMESELF>!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final Ability A=isPlant(myPlant);
				if(A!=null)	A.setInvoker(mob);
				mob.tell(L("You wrest control of @x1 from @x2.",myPlant.name(),myPlant.secretIdentity()));
				myPlant.setSecretIdentity(mob.Name());
			}

		}
		else
			beneficialVisualFizzle(mob,myPlant,L("<S-NAME> chant(s) to <T-NAMESELF>, but nothing happens."));


		// return whether it worked
		return success;
	}
}
