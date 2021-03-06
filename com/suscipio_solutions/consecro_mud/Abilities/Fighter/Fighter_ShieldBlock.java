package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Shield;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Fighter_ShieldBlock extends FighterSkill
{
	public int hits=0;
	@Override public String ID() { return "Fighter_ShieldBlock"; }
	private final static String localizedName = CMLib.lang().L("Shield Block");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){return "";}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_SHIELDUSE;}
	protected volatile int amountOfShieldArmor=-1;

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;

		if(msg.amITarget(mob)
		&&(amountOfShieldArmor>0)
		&&(msg.targetMinor()==CMMsg.TYP_WEAPONATTACK)
		&&(CMLib.flags().aliveAwakeMobileUnbound(mob,true))
		&&(msg.tool()!=null)
		&&(msg.tool() instanceof Weapon)
		&&(proficiencyCheck(null,mob.charStats().getStat(CharStats.STAT_DEXTERITY)-90+(2*getXLEVELLevel(mob)),false))
		&&(msg.source().getVictim()==mob))
		{
			final CMMsg msg2=CMClass.getMsg(msg.source(),mob,mob.fetchHeldItem(),CMMsg.MSG_QUIETMOVEMENT,L("<T-NAME> block(s) <S-YOUPOSS> attack with <O-NAME>!"));
			if(mob.location().okMessage(mob,msg2))
			{
				mob.location().send(mob,msg2);
				helpProficiency(mob, 0);
				return false;
			}
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

		if(msg.amISource(mob)&&(msg.target() instanceof Shield))
			amountOfShieldArmor=-1;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking, tickID))
			return false;
		if((amountOfShieldArmor<0)&&(tickID==Tickable.TICKID_MOB)&&(ticking instanceof MOB))
		{
			amountOfShieldArmor=0;
			for(final Enumeration<Item> i=((MOB)ticking).items(); i.hasMoreElements(); )
			{
				final Item I=i.nextElement();
				if((I instanceof Shield)
				&&(I.amWearingAt(Wearable.WORN_HELD)||I.amWearingAt(Wearable.WORN_WIELD))
				&&(I.owner()==ticking)
				&&(I.container() == null))
					amountOfShieldArmor+=I.phyStats().armor();
			}
			((MOB)ticking).recoverPhyStats();
		}
		return true;
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats stats)
	{
		super.affectPhyStats(affected,stats);
		if((affected instanceof MOB)&&(amountOfShieldArmor>0))
		{
			stats.setArmor(stats.armor()-(int)Math.round(CMath.mul(amountOfShieldArmor,CMath.mul(getXLEVELLevel((MOB)affected),0.5))));
		}
	}

	@Override
	public boolean autoInvocation(MOB mob)
	{
		amountOfShieldArmor=-1;
		return super.autoInvocation(mob);
	}
}
