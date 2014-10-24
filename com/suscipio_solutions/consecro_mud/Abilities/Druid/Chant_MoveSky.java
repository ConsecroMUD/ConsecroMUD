package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.TimeClock;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_MoveSky extends Chant
{
	@Override public String ID() { return "Chant_MoveSky"; }
	private final static String localizedName = CMLib.lang().L("Move The Sky");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){return "";}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_MOONSUMMONING;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int overrideMana(){return Ability.COST_ALL-99;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> chant(s), and the sky starts moving.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(mob.location().getArea().getTimeObj().getTODCode()==TimeClock.TimeOfDay.NIGHT)
				{
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("The moon begin(s) to descend!"));
					final int x=mob.location().getArea().getTimeObj().getHoursInDay()-mob.location().getArea().getTimeObj().getHourOfDay();
					mob.location().getArea().getTimeObj().tickTock(x);
				}
				else
				{
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("The sun hurries towards the horizon!"));
					final int x=mob.location().getArea().getTimeObj().getDawnToDusk()[TimeClock.TimeOfDay.NIGHT.ordinal()]-mob.location().getArea().getTimeObj().getHourOfDay();
					mob.location().getArea().getTimeObj().tickTock(x);
				}
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> chant(s), but the magic fades"));


		// return whether it worked
		return success;
	}
}
