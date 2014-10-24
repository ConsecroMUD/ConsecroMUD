package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class EternityLeafShield extends StdShield
{
	@Override public String ID(){	return "EternityLeafShield";}
	public EternityLeafShield()
	{
		super();

		setName("a huge leaf");
		setDisplayText("a huge and very rigid leaf lays on the ground.");
		setDescription("a very huge and very rigid leaf");
		secretIdentity="A shield made from one of the leaves of the Fox god\\`s Eternity Trees.  (Armor:  30)";
		properWornBitmap=Wearable.WORN_HELD;
		wornLogicalAnd=true;
		baseGoldValue+=15000;
		basePhyStats().setArmor(30);
		basePhyStats().setAbility(0);
		basePhyStats().setWeight(15);
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_BONUS);
		recoverPhyStats();
		material=RawMaterial.RESOURCE_SEAWEED;
	}


}
