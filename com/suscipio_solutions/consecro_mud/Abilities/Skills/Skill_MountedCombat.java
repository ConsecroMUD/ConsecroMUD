package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Skill_MountedCombat extends StdSkill
{
	@Override public String ID() { return "Skill_MountedCombat"; }
	private final static String localizedName = CMLib.lang().L("Mounted Combat");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_ANIMALAFFINITY;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected instanceof MOB)
		{
			final MOB mob=(MOB)affected;
			if((mob.isInCombat())&&(mob.rangeToTarget()==0)&&(mob.riding()!=null)&&(mob.riding().amRiding(mob)))
			{
				final int xlvl=super.getXLEVELLevel(invoker());
				affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+((1+(xlvl/3))*mob.basePhyStats().attackAdjustment()));
				affectableStats.setDamage(affectableStats.damage()+((1+(xlvl/3))*mob.basePhyStats().damage()));
			}
		}
	}
}
