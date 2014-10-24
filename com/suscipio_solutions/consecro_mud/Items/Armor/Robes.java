package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class Robes extends StdArmor
{
	@Override public String ID(){	return "Robes";}
	public Robes()
	{
		super();

		setName("a set of robes");
		setDisplayText("a set of robes is folded nice and neatly here.");
		setDescription("It is a finely crafted set of robes.");
		properWornBitmap=Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
		wornLogicalAnd=true;
		basePhyStats().setArmor(8);
		basePhyStats().setWeight(10);
		basePhyStats().setAbility(0);
		baseGoldValue=5;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_COTTON;
	}


}
