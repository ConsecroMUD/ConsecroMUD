package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class BandedArmor extends StdArmor
{
	@Override public String ID(){	return "BandedArmor";}
	public BandedArmor()
	{
		super();
		setName("a suit of banded armor");
		setDisplayText("a suit of armor made from metal bands fastened to leather");
		setDescription("This suit of armor is made from metal bands fastened to leather and will provide protection for the torso, arms, and legs.");
		properWornBitmap=Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
		wornLogicalAnd=true;
		basePhyStats().setArmor(44);
		basePhyStats().setWeight(55);
		basePhyStats().setAbility(0);
		baseGoldValue=400;
		material=RawMaterial.RESOURCE_IRON;
		recoverPhyStats();
	}

}
