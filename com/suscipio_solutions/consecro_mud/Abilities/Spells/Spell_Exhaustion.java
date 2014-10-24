package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_Exhaustion extends Spell
{
	@Override public String ID() { return "Spell_Exhaustion"; }
	private final static String localizedName = CMLib.lang().L("Exhaustion");
	@Override public String name() { return localizedName; }
	@Override public int maxRange(){return adjustedMaxInvokerRange(5);}
	@Override public int minRange(){return 1;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_ALTERATION;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(((MOB)target).curState().getMovement()<(((MOB)target).maxState().getMovement()/10))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

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
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L(auto?"":"^S<S-NAME> point(s) at <T-NAMESELF> and shout(s)!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				invoker=mob;
				if(msg.value()>0)
				{
					target.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<T-NAME> become(s) exhausted!"));
					target.curState().setMovement(0);
					if(target.maxState().getFatigue()>Long.MIN_VALUE/2)
						target.curState().setFatigue(target.curState().getFatigue()+CharState.FATIGUED_MILLIS);
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> point(s) and shout(s) at <T-NAMESELF>, but nothing more happens."));

		return success;
	}
}
