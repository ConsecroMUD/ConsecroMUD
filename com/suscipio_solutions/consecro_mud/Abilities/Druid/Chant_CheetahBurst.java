package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;




@SuppressWarnings("rawtypes")
public class Chant_CheetahBurst extends Chant
{
	@Override public String ID() { return "Chant_CheetahBurst"; }
	private final static String localizedName = CMLib.lang().L("Cheetah Burst");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Cheetah Burst)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_SHAPE_SHIFTING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	protected int cheetahTick=3;

	public Chant_CheetahBurst()
	{
		super();
		cheetahTick = 3;
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(cheetahTick==1)
			affectableStats.setSpeed(affectableStats.speed() + 3.0+CMath.mul(0.1,getXLEVELLevel(invoker())));
	}


	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		super.unInvoke();

		if(canBeUninvoked())
			mob.tell(L("You begin to slow down to a normal speed."));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		if(!(affected instanceof MOB))
			return true;
		final MOB mob=(MOB)affected;
		if((--cheetahTick)==0)
		{
			mob.recoverPhyStats();
			cheetahTick=3;
		}
		else
		if(cheetahTick==1)
			mob.recoverPhyStats();
		mob.curState().adjMovement(mob.charStats().getStat(CharStats.STAT_STRENGTH)/5,mob.maxState());
		return true;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(target==null) return false;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already at a cheetah's speed."));
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
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> chant(s) and snarl(s)!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(target.location()==mob.location())
				{
					target.location().show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> gain(s) cheetah-like reflexes!"));
					beneficialAffect(mob,target,asLevel,0);
					final Chant_CheetahBurst A=(Chant_CheetahBurst)target.fetchEffect(ID());
					if(A!=null) A.cheetahTick=3;
				}
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> chant(s) and snarl(s), but nothing more happens."));

		// return whether it worked
		return success;
	}
}
