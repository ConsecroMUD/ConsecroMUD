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


public class Skill_BellyRolling extends StdSkill
{
	@Override public String ID() { return "Skill_BellyRolling"; }
	private final static String localizedName = CMLib.lang().L("Belly Rolling");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_FOOLISHNESS;}
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
		&&(CMLib.flags().isSitting(mob))
		&&(msg.tool()!=null)
		&&(!doneThisRound)
		&&(msg.tool() instanceof Weapon))
		{
			// can't use -NAME for msg.source() lest sitting prevent it
			final CMMsg msg2=CMClass.getMsg(mob,msg.source(),null,CMMsg.MSG_SITMOVE,L("<S-NAME> roll(s) away from the attack by <T-NAMESELF>!"));
			if((proficiencyCheck(null,mob.charStats().getStat(CharStats.STAT_DEXTERITY)-50+(super.getXLEVELLevel(mob)*5),false))
			&&((msg.source().getVictim()==mob)||(msg.source().getVictim()==null))
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


