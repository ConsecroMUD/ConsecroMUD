package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class PaddedArmor extends StdArmor
{
	@Override public String ID(){	return "PaddedArmor";}
	public PaddedArmor()
	{
		super();

		setName("a suit of padded armor");
		setDisplayText("a suit of padded armor including everything needed to protect the torso, legs, and arms");
		setDescription("This is a fairly decent looking suit of padded armor");
		properWornBitmap=Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
		wornLogicalAnd=true;
		basePhyStats().setArmor(12);
		basePhyStats().setWeight(30);
		basePhyStats().setAbility(0);
		baseGoldValue=8;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_LEATHER;
	}

}
