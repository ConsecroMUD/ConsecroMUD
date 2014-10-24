package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class Hat extends StdArmor
{
	@Override public String ID(){	return "Hat";}
	public Hat()
	{
		super();

		setName("a feathered cap");
		setDisplayText("a feathered cap.");
		setDescription("It looks like a regular cap with long feather.");
		properWornBitmap=Wearable.WORN_HEAD;
		wornLogicalAnd=false;
		basePhyStats().setArmor(2);
		basePhyStats().setWeight(1);
		basePhyStats().setAbility(0);
		baseGoldValue=5;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_LEATHER;
	}

}
