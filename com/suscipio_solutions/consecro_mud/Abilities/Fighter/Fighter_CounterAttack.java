package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Fighter_CounterAttack extends FighterSkill
{
	@Override public String ID() { return "Fighter_CounterAttack"; }
	private final static String localizedName = CMLib.lang().L("Counter-Attack");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int classificationCode(){ return Ability.ACODE_SKILL|Ability.DOMAIN_MARTIALLORE;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;

		if(msg.amISource(mob)
		&&(CMLib.flags().aliveAwakeMobileUnbound(mob,true))
		&&(msg.target() instanceof MOB)
		&&(msg.tool() instanceof Ability)
		&&((mob.fetchAbility(ID())==null)||proficiencyCheck(mob,0,false))
		&&(mob.rangeToTarget()==0))
		{
			if(msg.tool().ID().equals("Skill_Parry"))
			{
				final CMMsg msg2=CMClass.getMsg(mob,msg.target(),this,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> position(s) <S-HIM-HERSELF> for a counterattack!"));
				msg.addTrailerMsg(msg2);
			}
			else
			if(msg.tool().ID().equals(ID()))
				CMLib.combat().postAttack(mob,(MOB)msg.target(),mob.fetchWieldedItem());
		}
		return true;
	}
}
