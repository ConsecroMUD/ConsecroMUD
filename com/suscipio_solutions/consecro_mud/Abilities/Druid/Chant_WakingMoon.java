package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.TimeClock;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;




@SuppressWarnings("rawtypes")
public class Chant_WakingMoon extends Chant
{
	@Override public String ID() { return "Chant_WakingMoon"; }
	private final static String localizedName = CMLib.lang().L("Waking Moon");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Waking Moon)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_MOONSUMMONING;}
	@Override public long flags(){return FLAG_WEATHERAFFECTING;}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(affected==null) return;
		if(canBeUninvoked())
		{
			final Room R=CMLib.map().roomLocation(affected);
			if((R!=null)&&(CMLib.flags().isInTheGame(affected,true)))
				R.showHappens(CMMsg.MSG_OK_VISUAL,L("The waking moon sets."));
		}
		super.unInvoke();

	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		if(affected==null) return false;
		if(affected instanceof Room)
		{
			final Room R=(Room)affected;
			if((R.getArea().getTimeObj().getTODCode()!=TimeClock.TimeOfDay.DAWN)
			&&(R.getArea().getTimeObj().getTODCode()!=TimeClock.TimeOfDay.DAY))
				unInvoke();
		}
		return true;
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			final Room R=mob.location();
			if((R!=null)&&(!R.getArea().getClimateObj().canSeeTheMoon(R,null)))
			{
				if((R.getArea().getTimeObj().getTODCode()!=TimeClock.TimeOfDay.DAWN)
				&&(R.getArea().getTimeObj().getTODCode()!=TimeClock.TimeOfDay.DAY))
					return Ability.QUALITY_INDIFFERENT;
				if((R.domainType()&Room.INDOORS)>0)
					return Ability.QUALITY_INDIFFERENT;
				if(R.fetchEffect(ID())!=null)
					return Ability.QUALITY_INDIFFERENT;
				return super.castingQuality(mob, target,Ability.QUALITY_BENEFICIAL_SELF);
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Room target=mob.location();
		if(target==null) return false;
		if((target.getArea().getTimeObj().getTODCode()!=TimeClock.TimeOfDay.DAWN)
		&&(target.getArea().getTimeObj().getTODCode()!=TimeClock.TimeOfDay.DAY))
		{
			mob.tell(L("You can only start this chant during the day."));
			return false;
		}
		if((target.domainType()&Room.INDOORS)>0)
		{
			mob.tell(L("This chant does not work indoors."));
			return false;
		}

		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(L("This place is already under the waking moon."));
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
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> chant(s) to the sky.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("The Waking Moon Rises!"));
					beneficialAffect(mob,target,asLevel,0);
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> chant(s) to the sky, but the magic fades."));
		// return whether it worked
		return success;
	}
}
