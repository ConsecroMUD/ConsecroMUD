package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class SteelGauntlets extends StdArmor
{
	@Override public String ID(){	return "SteelGauntlets";}
	public SteelGauntlets()
	{
		super();

		setName("some steel gauntlets");
		setDisplayText("a pair of steel gauntlets sit here.");
		setDescription("They look like they're made of steel.");
		properWornBitmap=Wearable.WORN_HANDS | Wearable.WORN_LEFT_WRIST | Wearable.WORN_RIGHT_WRIST;
		wornLogicalAnd=true;
		basePhyStats().setArmor(3); // = $$$$ =
		basePhyStats().setAbility(0);
		basePhyStats().setWeight(5);
		baseGoldValue=20;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
	}

}
