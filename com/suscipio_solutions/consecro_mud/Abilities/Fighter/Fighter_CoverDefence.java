package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Fighter_CoverDefence extends FighterSkill
{
	public int hits=0;
	@Override public String ID() { return "Fighter_CoverDefence"; }
	private final static String localizedName = CMLib.lang().L("Cover Defence");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){return "";}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int classificationCode(){ return Ability.ACODE_SKILL|Ability.DOMAIN_EVASIVE;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;

		if(msg.amITarget(mob)
		   &&(msg.targetMinor()==CMMsg.TYP_WEAPONATTACK)
		   &&(CMLib.flags().aliveAwakeMobile(mob,true))
		   &&(msg.source().rangeToTarget()>0)
		   &&(mob.phyStats().height()<84)
		   &&(msg.tool()!=null)
		   &&(msg.tool() instanceof Weapon)
		   &&((((Weapon)msg.tool()).weaponClassification()==Weapon.CLASS_RANGED)
			  ||(((Weapon)msg.tool()).weaponClassification()==Weapon.CLASS_THROWN))
		   &&(proficiencyCheck(null,mob.charStats().getStat(CharStats.STAT_DEXTERITY)-90+(2*getXLEVELLevel(mob)),false))
		   &&(msg.source().getVictim()==mob))
		{
			final CMMsg msg2=CMClass.getMsg(msg.source(),mob,null,CMMsg.MSG_QUIETMOVEMENT,L("<T-NAME> take(s) cover from <S-YOUPOSS> attack!"));
			if(mob.location().okMessage(mob,msg2))
			{
				mob.location().send(mob,msg2);
				helpProficiency(mob, 0);
				return false;
			}
		}
		return true;
	}
}
