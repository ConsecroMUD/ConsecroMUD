package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Spell_Blink extends Spell
{
	@Override public String ID() { return "Spell_Blink"; }
	private final static String localizedName = CMLib.lang().L("Blink");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Blink spell)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){	return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		super.unInvoke();

		if(canBeUninvoked())
			if((mob.location()!=null)&&(!mob.amDead()))
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> stop(s) blinking."));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((tickID==Tickable.TICKID_MOB)&&(affected!=null)&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			final int roll=CMLib.dice().roll(1,8,0);
			if(mob.isInCombat())
			{
				int move=0;
				switch(roll)
				{
				case 1: move=-2; break;
				case 2: move=-1; break;
				case 7: move=1; break;
				case 8: move=2; break;
				default: move=0;
				}
				if(move==0)
					mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> vanish(es) and reappear(s) again."));
				else
				{
					int rangeTo=mob.rangeToTarget();
					rangeTo+=move;
					if((move==0)||(rangeTo<0)||(rangeTo>mob.location().maxRange()))
						move=0;
					else
					{
						mob.setAtRange(rangeTo);
						if(mob.getVictim().getVictim()==mob)
							mob.getVictim().setAtRange(rangeTo);
					}
					switch(move)
					{
					case 0:
						mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> vanish(es) and reappear(s) again."));
						break;
					case 1:
						mob.location().show(mob,null,mob.getVictim(),CMMsg.MSG_OK_VISUAL,L("<S-NAME> vanish(es) and reappear(s) a bit further from <O-NAMESELF>."));
						break;
					case 2:
						mob.location().show(mob,null,mob.getVictim(),CMMsg.MSG_OK_VISUAL,L("<S-NAME> vanish(es) and reappear(s) much further from <O-NAMESELF>."));
						break;
					case -1:
						mob.location().show(mob,null,mob.getVictim(),CMMsg.MSG_OK_VISUAL,L("<S-NAME> vanish(es) and reappear(s) a bit closer to <O-NAMESELF>."));
						break;
					case -2:
						mob.location().show(mob,null,mob.getVictim(),CMMsg.MSG_OK_VISUAL,L("<S-NAME> vanish(es) and reappear(s) much closer to <O-NAMESELF>."));
						break;
					}
				}
				if(mob.getVictim()==null) mob.setVictim(null); // correct range
			}
			else
			if((roll>2)&&(roll<7))
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> vanish(es) and reappear(s) a few feet away."));
			else
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> vanish(es) and reappear(s) again."));
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> begin(s) to blink!"):L("^S<S-NAME> cast(s) a spell at <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(target.location()==mob.location())
					if((mob.phyStats().level()+(2*getXLEVELLevel(mob)))>5)
						success=beneficialAffect(mob,target,asLevel,(mob.phyStats().level()+(2*getXLEVELLevel(mob)))-4)!=null;
					else
						success=beneficialAffect(mob,target,asLevel,mob.phyStats().level()+(2*getXLEVELLevel(mob)))!=null;
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> cast(s) a spell to <T-NAMESELF>, but the magic fizzles."));

		// return whether it worked
		return success;
	}
}
