package com.suscipio_solutions.consecro_mud.Abilities.Paladin;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Paladin_DiseaseImmunity extends PaladinSkill
{
	@Override public String ID() { return "Paladin_DiseaseImmunity"; }
	private final static String localizedName = CMLib.lang().L("Disease Immunity");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){ return Ability.ACODE_SKILL|Ability.DOMAIN_HOLYPROTECTION;}


	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;
		if((msg.amITarget(mob))
		&&(msg.targetMinor()==CMMsg.TYP_DISEASE)
		&&(!mob.amDead())
		&&(CMLib.flags().isGood(mob))
		&&((invoker==null)||(invoker.fetchAbility(ID())==null)||proficiencyCheck(null,0,false)))
			return false;
		return super.okMessage(myHost,msg);
	}
	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if((affected!=null)&&(CMLib.flags().isGood(affected)))
			affectableStats.setStat(CharStats.STAT_SAVE_DISEASE,affectableStats.getStat(CharStats.STAT_SAVE_DISEASE)+50+proficiency()+(5*getXLEVELLevel(affected)));
	}
}
