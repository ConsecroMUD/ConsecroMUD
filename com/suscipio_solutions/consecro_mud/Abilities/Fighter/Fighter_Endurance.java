package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Fighter_Endurance extends FighterSkill
{
	@Override public String ID() { return "Fighter_Endurance"; }
	private final static String localizedName = CMLib.lang().L("Endurance");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int classificationCode() {   return Ability.ACODE_SKILL|Ability.DOMAIN_FITNESS; }

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!(affected instanceof MOB))
			return super.tick(ticking,tickID);

		final MOB mob=(MOB)affected;

		if(((CMLib.flags().isSitting(mob))||(CMLib.flags().isSleeping(mob)))
		&&(!mob.isInCombat())
		&&((mob.fetchAbility(ID())==null)||proficiencyCheck(null,0,false))
		&&(tickID==Tickable.TICKID_MOB))
		{
			final int bonus=(getXLEVELLevel(mob)/3)+1;
			for(int x=0;x<bonus;x++)
				CMLib.combat().recoverTick(mob);
			helpProficiency(mob, 0);
		}
		return super.tick(ticking,tickID);
	}
}
