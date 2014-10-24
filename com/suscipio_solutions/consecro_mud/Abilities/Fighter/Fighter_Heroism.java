package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Fighter_Heroism extends FighterSkill
{
	@Override public String ID() { return "Fighter_Heroism"; }
	private final static String localizedName = CMLib.lang().L("Heroism");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_MARTIALLORE;}
	private boolean activated=false;

	public void setActivated(boolean activate)
	{
		if(activate==activated)
			return;
		activated=activate;
		if(affected instanceof MOB)
			((MOB)affected).recoverCharStats();
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!(affected instanceof MOB))
			return super.tick(ticking,tickID);

		final MOB mob=(MOB)affected;

		if((CMLib.flags().isStanding(mob))
		&&(mob.isInCombat())
		&&((mob.fetchAbility(ID())==null)||proficiencyCheck(null,0,false))
		&&(tickID==Tickable.TICKID_MOB))
		{
			setActivated(true);
			if(CMLib.dice().rollPercentage()==1)
				helpProficiency(mob, 0);
		}
		else
			setActivated(false);
		return super.tick(ticking,tickID);
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		affectableStats.setStat(CharStats.STAT_SAVE_JUSTICE,
								affectableStats.getStat(CharStats.STAT_SAVE_JUSTICE)
								+(affectableStats.getStat(CharStats.STAT_CHARISMA)/4)
								+(affectableStats.getStat(CharStats.STAT_STRENGTH)/4)
								+(adjustedLevel(affected,0)/2));
	}
}
