package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class Skill_AllBreathing extends StdSkill
{
	@Override public String ID() { return "Skill_AllBreathing"; }
	private final static String localizedName = CMLib.lang().L("All Breathing");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		if(!CMLib.flags().canBreathe(mob))
			affectableStats.setSensesMask(affectableStats.sensesMask()-PhyStats.CAN_NOT_BREATHE);
	}
	private static final int[] allBreathe=new int[0];
	@Override
	public void affectCharStats(MOB affectedMob, CharStats affectableStats)
	{
		super.affectCharStats(affectedMob, affectableStats);
		affectableStats.setBreathables(allBreathe);
	}
}
