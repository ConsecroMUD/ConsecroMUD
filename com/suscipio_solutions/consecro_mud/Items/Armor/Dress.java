package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class Dress extends StdArmor
{
	@Override public String ID(){	return "Dress";}
	public Dress()
	{
		super();

		setName("a nice dress");
		setDisplayText("a nice dress has been left here.");
		setDescription("Well and neatly made, this plain dress would look fine on just about anyone.");
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
