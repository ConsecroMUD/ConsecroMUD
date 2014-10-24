package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class Skill_Dodge extends StdSkill
{
	@Override public String ID() { return "Skill_Dodge"; }
	private final static String localizedName = CMLib.lang().L("Dodge");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int classificationCode(){ return Ability.ACODE_SKILL|Ability.DOMAIN_EVASIVE;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	protected boolean doneThisRound=false;

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(tickID==Tickable.TICKID_MOB)
			doneThisRound=false;
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;

		if(msg.amITarget(mob)
		&&(msg.targetMinor()==CMMsg.TYP_WEAPONATTACK)
		&&(CMLib.flags().aliveAwakeMobile(mob,true))
		&&(msg.source().rangeToTarget()==0)
		&&((msg.tool()==null)
			||((msg.tool() instanceof Weapon)
			  &&(((Weapon)msg.tool()).weaponClassification()!=Weapon.CLASS_RANGED)
			  &&(((Weapon)msg.tool()).weaponClassification()!=Weapon.CLASS_THROWN))))
		{
			final CMMsg msg2=CMClass.getMsg(mob,msg.source(),null,CMMsg.MSG_QUIETMOVEMENT,L("<S-NAME> dodge(s) the attack by <T-NAME>!"));
			if((proficiencyCheck(null,mob.charStats().getStat(CharStats.STAT_DEXTERITY)-93+(getXLEVELLevel(mob)),false))
			&&(msg.source().getVictim()==mob)
			&&(!doneThisRound)
			&&(mob.location().okMessage(mob,msg2)))
			{
				doneThisRound=true;
				mob.location().send(mob,msg2);
				helpProficiency(mob, 0);
				return false;
			}
		}
		return true;
	}
}
