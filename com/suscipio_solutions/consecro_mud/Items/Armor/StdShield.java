package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Shield;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class StdShield extends StdArmor implements Armor, Shield
{
	@Override public String ID(){	return "StdShield";}
	public StdShield()
	{
		super();

		setName("a shield");
		setDisplayText("a sturdy round shield sits here.");
		setDescription("Its made of steel, and looks in good shape.");
		properWornBitmap=Wearable.WORN_HELD;
		wornLogicalAnd=false;
		basePhyStats().setArmor(10);
		basePhyStats().setAbility(0);
		basePhyStats().setWeight(15);
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
	}


}
