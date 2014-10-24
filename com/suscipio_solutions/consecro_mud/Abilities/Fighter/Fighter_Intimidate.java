package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Fighter_Intimidate extends FighterSkill
{
	@Override public String ID() { return "Fighter_Intimidate"; }
	private final static String localizedName = CMLib.lang().L("Intimidation");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int classificationCode() {   return Ability.ACODE_SKILL|Ability.DOMAIN_INFLUENTIAL; }
	public Room lastRoom=null;

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(((msg.targetMajor()&CMMsg.MASK_MALICIOUS)>0)
		&&(!CMath.bset(msg.sourceMajor(),CMMsg.MASK_ALWAYS))
		&&((msg.amITarget(affected))))
		{
			final MOB targetM=(MOB)msg.target();
			final MOB attackerM=msg.source();
			final int levelDiff=((attackerM.phyStats().level()-(targetM.phyStats().level()+((2*getXLEVELLevel(targetM)))))*10);
			// 1 level off = -10
			// 10 levels off = -100
			if((!targetM.isInCombat())
			&&(msg.source().getVictim()!=targetM)
			&&(levelDiff<0)
			&&(attackerM.location()==targetM.location())
			&&((targetM.fetchAbility(ID())==null)||proficiencyCheck(null,(-(100+levelDiff))+(targetM.charStats().getStat(CharStats.STAT_CHARISMA)*2),false)))
			{
				attackerM.tell(L("You are too intimidated by @x1",targetM.name(attackerM)));
				if(targetM.location()!=lastRoom)
				{
					lastRoom=targetM.location();
					helpProficiency(targetM, 0);
				}
				if(targetM.getVictim()==msg.source())
				{
					targetM.makePeace();
					targetM.setVictim(null);
				}
				return false;
			}
		}
		return super.okMessage(myHost,msg);
	}

}
