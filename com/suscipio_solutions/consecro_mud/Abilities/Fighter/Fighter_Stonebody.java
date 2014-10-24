package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Fighter_Stonebody extends FighterSkill
{
	int regain=-1;
	@Override public String ID() { return "Fighter_Stonebody"; }
	private final static String localizedName = CMLib.lang().L("Stone Body");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int classificationCode() {   return Ability.ACODE_SKILL|Ability.DOMAIN_FITNESS; }

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		regain=-1;
		if(!super.okMessage(myHost,msg))
			return false;

		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;
		if(msg.amITarget(mob)
		&&(CMLib.flags().aliveAwakeMobile(mob,true))
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&((msg.value())>0)
		&&(msg.tool()!=null)
		&&(msg.tool() instanceof Weapon)
		&&(mob.rangeToTarget()==0)
		&&((mob.fetchAbility(ID())==null)||proficiencyCheck(null,-85+mob.charStats().getStat(CharStats.STAT_CONSTITUTION),false)))
		{
			final float f=getXLEVELLevel(mob);
			final int regain=(int)Math.round(CMath.mul(CMath.div(proficiency(),100.0),2.0+(0.15*f)));
			msg.setValue(msg.value()-regain);
		}
		return true;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);

		if(!(affected instanceof MOB))
			return;

		final MOB mob=(MOB)affected;
		if((msg.amITarget(mob))
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&(regain>0))
		{
			helpProficiency(mob, 0);
			regain=-1;
		}
	}
}
