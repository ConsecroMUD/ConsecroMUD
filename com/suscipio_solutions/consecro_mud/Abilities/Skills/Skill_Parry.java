package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class Skill_Parry extends StdSkill
{
	@Override public String ID() { return "Skill_Parry"; }
	private final static String localizedName = CMLib.lang().L("Parry");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_EVASIVE;}
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
		   &&(CMLib.flags().aliveAwakeMobileUnbound(mob,true))
		   &&(msg.targetMinor()==CMMsg.TYP_WEAPONATTACK)
		   &&(!doneThisRound)
		   &&(mob.rangeToTarget()==0))
		{
			if((msg.tool()!=null)&&(msg.tool() instanceof Item))
			{
				final Item attackerWeapon=(Item)msg.tool();
				final Item myWeapon=mob.fetchWieldedItem();
				if((myWeapon!=null)
				&&(attackerWeapon!=null)
				&&(myWeapon instanceof Weapon)
				&&(attackerWeapon instanceof Weapon)
				&&(CMLib.flags().canBeSeenBy(msg.source(),mob))
				&&(((Weapon)myWeapon).weaponClassification()!=Weapon.CLASS_FLAILED)
				&&(((Weapon)myWeapon).weaponClassification()!=Weapon.CLASS_RANGED)
				&&(((Weapon)myWeapon).weaponClassification()!=Weapon.CLASS_THROWN)
				&&(((Weapon)myWeapon).weaponClassification()!=Weapon.CLASS_NATURAL)
				&&(((Weapon)attackerWeapon).weaponClassification()!=Weapon.CLASS_FLAILED)
				&&(((Weapon)attackerWeapon).weaponClassification()!=Weapon.CLASS_NATURAL)
				&&(((Weapon)attackerWeapon).weaponClassification()!=Weapon.CLASS_RANGED)
				&&(((Weapon)attackerWeapon).weaponClassification()!=Weapon.CLASS_THROWN))
				{
					final CMMsg msg2=CMClass.getMsg(mob,msg.source(),this,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> parr(ys) @x1 attack from <T-NAME>!",attackerWeapon.name()));
					if((proficiencyCheck(null,mob.charStats().getStat(CharStats.STAT_DEXTERITY)-90+(getXLEVELLevel(mob)),false))
					&&(mob.location().okMessage(mob,msg2)))
					{
						doneThisRound=true;
						mob.location().send(mob,msg2);
						helpProficiency(mob, 0);
						return false;
					}
				}
			}
		}
		return true;
	}
}
