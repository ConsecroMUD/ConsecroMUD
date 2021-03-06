package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.MendingSkill;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_Invigorate extends Prayer implements MendingSkill
{
	@Override public String ID() { return "Prayer_Invigorate"; }
	private final static String localizedName = CMLib.lang().L("Invigorate");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_RESTORATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override public long flags(){return Ability.FLAG_HOLY;}
	@Override protected long minCastWaitTime(){return CMProps.getTickMillis()/2;}

	@Override
	public boolean supportsMending(Physical item)
	{
		return (item instanceof MOB)
				&&(((((MOB)item).curState()).getFatigue()>0)
						||((((MOB)item).curState()).getMovement()<(((MOB)item).maxState()).getMovement()));
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(!supportsMending(target))
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
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("A soft white glow surrounds <T-NAME>."):L("^S<S-NAME> @x1, delivering a strong invigorating touch to <T-NAMESELF>.^?",prayWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final int healing=CMLib.dice().roll(10,adjustedLevel(mob,asLevel),50);
				target.curState().setFatigue(0);
				target.curState().adjMovement(healing,target.maxState());
				target.tell(L("You feel really invigorated!"));
				lastCastHelp=System.currentTimeMillis();
			}
		}
		else
			beneficialWordsFizzle(mob,target,auto?"":L("<S-NAME> @x1 for <T-NAMESELF>, but nothing happens.",prayWord(mob)));
		// return whether it worked
		return success;
	}
}
