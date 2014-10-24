package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class LeatherBoots extends StdArmor
{
	@Override public String ID(){	return "LeatherBoots";}
	public LeatherBoots()
	{
		super();

		setName("a pair of leather boots");
		setDisplayText("a pair of leather boots sits here.");
		setDescription("They look like a rather nice pair of footwear.");
		properWornBitmap=Wearable.WORN_FEET;
		wornLogicalAnd=false;
		basePhyStats().setArmor(1);
		basePhyStats().setWeight(1);
		basePhyStats().setAbility(0);
		baseGoldValue=5;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_LEATHER;
	}


}
