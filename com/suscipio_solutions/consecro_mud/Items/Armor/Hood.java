package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class Hood extends StdArmor
{
	@Override public String ID(){	return "Hood";}
	public Hood()
	{
		super();

		setName("a cloth hood");
		setDisplayText("a cloth hood sits here.");
		setDescription("This is a cloth hood that covers the head and shoulders.");
		properWornBitmap=Wearable.WORN_HEAD;
		wornLogicalAnd=false;
		basePhyStats().setArmor(2);
		basePhyStats().setWeight(1);
		basePhyStats().setAbility(0);
		baseGoldValue=5;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_COTTON;
	}

}
