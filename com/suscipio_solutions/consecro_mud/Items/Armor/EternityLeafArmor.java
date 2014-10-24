package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class EternityLeafArmor extends StdArmor
{
	@Override public String ID(){	return "EternityLeafArmor";}
	public EternityLeafArmor()
	{
		super();

		setName("a suit of Eternity Tree Leaf Armor");
		setDisplayText("a suit of Eternity tree leaf armor sits here.");
		setDescription("This suit of armor is made from the leaves of the Eternity Tree, a true gift from the Fox god himself.  (armor:  50, grants a modest degree of stealth, and is as light as cloth.)");
		properWornBitmap=Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
		wornLogicalAnd=true;
		baseGoldValue+=25000;
		basePhyStats().setArmor(50);
		basePhyStats().setAbility(0);
		basePhyStats().setWeight(15);
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_BONUS);
		recoverPhyStats();
		material=RawMaterial.RESOURCE_SEAWEED;
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if((!this.amWearingAt(Wearable.IN_INVENTORY))&&(!this.amWearingAt(Wearable.WORN_HELD)))
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_SNEAKING);
	}


}
