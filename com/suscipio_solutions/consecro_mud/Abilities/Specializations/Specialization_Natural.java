package com.suscipio_solutions.consecro_mud.Abilities.Specializations;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class Specialization_Natural extends Specialization_Weapon
{
	@Override public String ID() { return "Specialization_Natural"; }
	private final static String localizedName = CMLib.lang().L("Hand to hand combat");
	@Override public String name() { return localizedName; }
	public Specialization_Natural()
	{
		super();
		weaponClass=Weapon.CLASS_NATURAL;
	}
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((activated)
		&&(msg.targetMinor()==CMMsg.TYP_WEAPONATTACK)
		&&(CMLib.dice().rollPercentage()<10)
		&&(affected instanceof MOB)
		&&(msg.amISource((MOB)affected))
		&&((!(msg.tool() instanceof Weapon))||(((Weapon)msg.tool()).weaponClassification()==Weapon.CLASS_NATURAL)))
			helpProficiency((MOB)affected, 0);
		super.executeMsg(myHost, msg);
	}


	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		activated=false;
		super.affectPhyStats(affected,affectableStats);
		if((affected instanceof MOB)&&(((MOB)affected).fetchWieldedItem()==null))
		{
			activated=true;
			affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()
					+(int)Math.round(15.0*(CMath.div(proficiency(),100.0)))
					+(10*(getXLEVELLevel((MOB)affected))));
		}
	}

	@Override
	protected boolean canDamage(MOB mob, Weapon W)
	{
		return (W.weaponClassification()==Weapon.CLASS_NATURAL) || (!W.amWearingAt(Wearable.IN_INVENTORY));
	}

}
