package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class LeatherGauntlets extends StdArmor
{
	@Override public String ID(){	return "LeatherGauntlets";}
	public LeatherGauntlets()
	{
		super();

		setName("Leather Gauntlets");
		setDisplayText("a pair of leather gauntlets.");
		setDescription("They look like they're made of doeskin.");
		properWornBitmap=Wearable.WORN_HANDS | Wearable.WORN_LEFT_WRIST | Wearable.WORN_RIGHT_WRIST;
		wornLogicalAnd=true;
		basePhyStats().setArmor(1);
		basePhyStats().setWeight(5);
		basePhyStats().setAbility(0);
		baseGoldValue=5;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_LEATHER;
	}

}
