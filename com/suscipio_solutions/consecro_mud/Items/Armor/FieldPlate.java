package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class FieldPlate extends StdArmor
{
	@Override public String ID(){	return "FieldPlate";}
	public FieldPlate()
	{
		super();

		setName("suit of Field Plate");
		setDisplayText("a suit of field plate Armor.");
		setDescription("A suit of field plate Armor including everything to protect the body, legs and arms.");
		properWornBitmap=Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
		wornLogicalAnd=true;
		basePhyStats().setArmor(59);
		basePhyStats().setWeight(80);
		basePhyStats().setAbility(0);
		baseGoldValue=4000;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
	}

}
