package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class HideArmor extends StdArmor
{
	@Override public String ID(){	return "HideArmor";}
	public HideArmor()
	{
		super();

		setName("suit of Hide Armor");
		setDisplayText("a suit of armor made from animal hides");
		setDescription("A suit of armor made from animal hides including everything to protect the body, legs and arms.");
		properWornBitmap=Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
		wornLogicalAnd=true;
		basePhyStats().setArmor(29);
		basePhyStats().setWeight(15);
		basePhyStats().setAbility(0);
		baseGoldValue=30;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_LEATHER;
	}

}
