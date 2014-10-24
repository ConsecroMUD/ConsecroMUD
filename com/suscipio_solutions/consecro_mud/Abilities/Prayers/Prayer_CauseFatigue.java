package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_CauseFatigue extends Prayer
{
	@Override public String ID() { return "Prayer_CauseFatigue"; }
	private final static String localizedName = CMLib.lang().L("Cause Fatigue");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_VEXING;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MASK_MALICIOUS|verbalCastCode(mob,target,auto),L(auto?"A light fatigue overcomes <T-NAME>.":"^S<S-NAME> "+prayWord(mob)+" for light fatigue to overcome <T-NAMESELF>!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					final int harming=CMLib.dice().roll(3,adjustedLevel(mob,asLevel),10);
					if(target.maxState().getFatigue()>Long.MIN_VALUE/2)
						target.curState().adjFatigue((target.curState().getFatigue()/2),target.maxState());
					target.curState().adjMovement(-harming,target.maxState());
					target.tell(L("You feel slightly more fatigued!"));
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> point(s) at <T-NAMESELF> and @x1, but nothing happens.",prayWord(mob)));

		// return whether it worked
		return success;
	}
}
