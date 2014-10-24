package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class WalkingBoots extends StdArmor
{
	@Override public String ID(){	return "WalkingBoots";}
	public WalkingBoots()
	{
		super();

		setName("a pair of nice hide walking boots");
		setDisplayText("a pair of hide walking boots sits here.");
		setDescription("They look like a rather nice pair of footwear.");
		properWornBitmap=Wearable.WORN_FEET;
		wornLogicalAnd=false;
		basePhyStats().setArmor(1);
		basePhyStats().setAbility(0);
		basePhyStats().setWeight(5);
		baseGoldValue=5;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_LEATHER;
	}


}
