package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class GlovesSpeed extends StdArmor
{
	@Override public String ID(){	return "GlovesSpeed";}
	public GlovesSpeed()
	{
		super();

		setName("a pair of gloves");
		setDisplayText("a pair of finely crafted gloves is found on the ground.");
		setDescription("This is a pair of very nice gloves.");
		secretIdentity="Gloves of the blinding strike (Double attack speed, truly usable only by fighters.)";
		baseGoldValue+=10000;
		properWornBitmap=Wearable.WORN_HANDS;
		wornLogicalAnd=false;
		basePhyStats().setArmor(15);
		basePhyStats().setAbility(0);
		basePhyStats().setWeight(1);
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_BONUS);
		recoverPhyStats();

	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if((!this.amWearingAt(Wearable.IN_INVENTORY))&&(!this.amWearingAt(Wearable.WORN_HELD)))
		{
			affectableStats.setSpeed(affectableStats.speed() * 2.0);
			affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() + 10);
		}
	}


}
