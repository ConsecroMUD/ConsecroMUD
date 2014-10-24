package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_Distract extends ThiefSkill
{
	@Override public String ID() { return "Thief_Distract"; }
	private final static String localizedName = CMLib.lang().L("Distract");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Distracted)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	private static final String[] triggerStrings =I(new String[] {"DISTRACT"});
	@Override public int classificationCode() {   return Ability.ACODE_SKILL|Ability.DOMAIN_DECEPTIVE; }
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	public int code=0;
	@Override public int abilityCode(){return code;}
	@Override public void setAbilityCode(int newCode){code=newCode;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		final float f=(float)0.05*super.getXLEVELLevel(invoker());
		affectableStats.setArmor(affectableStats.armor()+super.getXLEVELLevel(invoker())+30+abilityCode());
		affectableStats.setAttackAdjustment((affectableStats.attackAdjustment()-(int)Math.round(CMath.div(affectableStats.attackAdjustment(),2.0-f)))-abilityCode());
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		final MOB invoker=this.invoker;
		final Physical affected=this.affected;
		if((affected==null)||(!(affected instanceof MOB))||(invoker==null))
			return true;

		final MOB mob=(MOB)affected;
		if(invoker.location()!=mob.location())
			unInvoke();
		else
		{
			// preventing distracting player from doin anything else
			if(msg.amISource(invoker)
			&&(CMLib.dice().rollPercentage()>(mob.charStats().getStat(CharStats.STAT_WISDOM)*2))
			&&(msg.targetMinor()==CMMsg.TYP_WEAPONATTACK))
			{
				invoker.location().show(invoker,mob,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> distract(s) <T-NAME>."));
				return false;
			}
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		if(canBeUninvoked())
		{
			if(!mob.amDead())
			{
				if((invoker!=null)&&(invoker.location()==mob.location())&&(!invoker.amDead()))
					invoker.tell(invoker,mob,null,L("You are no longer distracting <T-NAMESELF>."));
				if((mob.location()!=null)&&(!mob.amDead()))
					mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> <S-IS-ARE> no longer so distracted."));
			}
		}
		super.unInvoke();
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if((mob!=null)&&(target!=null))
		{
			if((CMLib.flags().isSitting(mob)||CMLib.flags().isSleeping(mob)))
				return Ability.QUALITY_INDIFFERENT;
			if(!CMLib.flags().aliveAwakeMobileUnbound(mob,true))
				return Ability.QUALITY_INDIFFERENT;
			if(mob.isInCombat()&&(mob.rangeToTarget()>0))
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if((CMLib.flags().isSitting(mob)||CMLib.flags().isSleeping(mob)))
		{
			mob.tell(L("You are on the floor!"));
			return false;
		}

		if(!CMLib.flags().aliveAwakeMobileUnbound(mob,false))
			return false;
		if(mob.isInCombat()&&(mob.rangeToTarget()>0))
		{
			mob.tell(L("You are too far away to distract @x1!",mob.getVictim().name()));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int levelDiff=target.phyStats().level()-(mob.phyStats().level()+abilityCode()+(2*super.getXLEVELLevel(mob)));
		if(levelDiff>0)
			levelDiff=levelDiff*5;
		else
			levelDiff=0;
		final boolean success=proficiencyCheck(mob,-levelDiff,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MASK_MALICIOUS|CMMsg.MSG_THIEF_ACT,auto?L("<T-NAME> seem(s) distracted!"):L("<S-NAME> distract(s) <T-NAMESELF>!"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,4);
			}
		}
		else
			return beneficialVisualFizzle(mob,target,L("<S-NAME> attempt(s) to distract <T-NAMESELF>, but flub(s) it."));
		return success;
	}
}
