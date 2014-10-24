package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

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
public class Spell_Ignite extends Spell
{
	@Override public String ID() { return "Spell_Ignite"; }
	private final static String localizedName = CMLib.lang().L("Ignite");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){return "Ignite";}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canTargetCode(){return CAN_ITEMS|CAN_MOBS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_EVOCATION;}

	public void ignite(MOB mob, Item I)
	{
		int durationOfBurn=5;
		switch(I.material()&RawMaterial.MATERIAL_MASK)
		{
		case RawMaterial.MATERIAL_LEATHER:
			durationOfBurn=20+I.phyStats().weight();
			break;
		case RawMaterial.MATERIAL_CLOTH:
		case RawMaterial.MATERIAL_SYNTHETIC:
		case RawMaterial.MATERIAL_PAPER:
			durationOfBurn=5+I.phyStats().weight();
			break;
		case RawMaterial.MATERIAL_WOODEN:
			durationOfBurn=40+(I.phyStats().weight()*2);
			break;
		default:
			switch(I.material())
			{
			case RawMaterial.RESOURCE_COAL:
				durationOfBurn=20*(1+I.phyStats().weight()*3);
				break;
			case RawMaterial.RESOURCE_LAMPOIL:
				durationOfBurn=5+I.phyStats().weight();
				break;
			default:
				return;
			}
			break;
		}
		mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("@x1 ignites!",I.name()));
		final Ability B=CMClass.getAbility("Burning");
		if(B!=null)
			B.invoke(mob,I,true,durationOfBurn);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_UNWORNONLY);
		if(target==null) return false;
		if((!(target instanceof MOB))
		&&(!(target instanceof Item)))
		{
			mob.tell(L("You can't ignite '@x1'!",target.name(mob)));
			return false;
		}


		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> flares up!"):L("^S<S-NAME> evoke(s) a spell upon <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					if(target instanceof Item)
						ignite(mob,(Item)target);
					else
					if(target instanceof MOB)
					{
						final MOB mob2=(MOB)target;
						for(int i=0;i<mob2.numItems();i++)
						{
							final Item I=mob2.getItem(i);
							if((I!=null)&&(I.container()==null))
								ignite(mob2,I);
						}
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> evoke(s) at <T-NAMESELF>, but the spell fizzles."));

		// return whether it worked
		return success;
	}
}
