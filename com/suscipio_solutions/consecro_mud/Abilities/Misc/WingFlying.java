package com.suscipio_solutions.consecro_mud.Abilities.Misc;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.HealthCondition;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class WingFlying extends StdAbility implements HealthCondition
{
	@Override public String ID() { return "WingFlying"; }
	private final static String localizedName = CMLib.lang().L("Winged Flight");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public boolean putInCommandlist(){return false;}
	private static final String[] triggerStrings =I(new String[] {"FLAP"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_RACIALABILITY;}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public String getHealthConditionDesc()
	{
		return "Weak Paralysis";
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected==null) return;
		if(!(affected instanceof MOB)) return;

		if(!CMLib.flags().isSleeping(affected))
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_FLYING);
		else
			affectableStats.setDisposition(CMath.unsetb(affectableStats.disposition(),PhyStats.IS_FLYING));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((tickID==Tickable.TICKID_MOB)&&(ticking instanceof MOB)&&(((MOB)ticking).charStats().getBodyPart(Race.BODY_WING)<=0))
			unInvoke();
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=mob;
		if(target==null) return false;
		if(target.charStats().getBodyPart(Race.BODY_WING)<=0)
		{
			mob.tell(L("You can't flap without wings."));
			return false;
		}

		final boolean wasFlying=CMLib.flags().isFlying(target);
		Ability A=target.fetchEffect(ID());
		if(A!=null) A.unInvoke();
		target.recoverPhyStats();
		String str="";
		if(wasFlying)
			str=L("<S-NAME> stop(s) flapping <S-HIS-HER> wings.");
		else
			str=L("<S-NAME> start(s) flapping <S-HIS-HER> wings.");


		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_NOISYMOVEMENT,str);
			if(target.location().okMessage(target,msg))
			{
				target.location().send(target,msg);
				beneficialAffect(mob,target,asLevel,9999);
				A=target.fetchEffect(ID());
				if(A!=null) A.makeLongLasting();
			}
		}
		else
			return beneficialVisualFizzle(mob,target,L("<T-NAME> fumble(s) trying to use <T-HIS-HER> wings."));


		// return whether it worked
		return success;
	}
}
