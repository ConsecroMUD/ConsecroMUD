package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Druid_KnowPlants extends StdAbility
{
	@Override public String ID() { return "Druid_KnowPlants"; }
	private final static String localizedName = CMLib.lang().L("Know Plants");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS;}
	private static final String[] triggerStrings =I(new String[] {"KNOWPLANT"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_NATURELORE;}

	public static boolean isPlant(Item I)
	{
		if((I!=null)&&(I.rawSecretIdentity().length()>0))
		{
			for(final Enumeration<Ability> a=I.effects();a.hasMoreElements();)
			{
				final Ability A=a.nextElement();
				if((A!=null)&&(A.invoker()!=null)&&(A instanceof Chant_SummonPlants))
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Item I=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_UNWORNONLY);
		if(I==null) return false;
		if(((I.material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_VEGETATION)
		&&((I.material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_WOODEN))
		{
			mob.tell(L("Your plant knowledge can tell you nothing about @x1.",I.name(mob)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		final boolean success=proficiencyCheck(mob,0,auto);

		if(!success)
			mob.tell(L("Your plant senses fail you."));
		else
		{
			final CMMsg msg=CMClass.getMsg(mob,I,null,CMMsg.MSG_DELICATE_SMALL_HANDS_ACT|CMMsg.MASK_MAGIC,null);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final StringBuffer str=new StringBuffer("");
				str.append(L("@x1 is a kind of @x2.  ",I.name(mob),RawMaterial.CODES.NAME(I.material()).toLowerCase()));
				if(isPlant(I))
					str.append(L("It was summoned by @x1.",I.rawSecretIdentity()));
				else
					str.append(L("It is either processed by hand, or grown wild."));
				mob.tell(str.toString());
			}
		}
		return success;
	}
}

