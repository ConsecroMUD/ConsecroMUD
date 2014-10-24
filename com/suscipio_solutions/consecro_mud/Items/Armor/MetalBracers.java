package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class MetalBracers extends StdArmor
{
	@Override public String ID(){	return "MetalBracers";}
	public MetalBracers()
	{
		super();

		setName("a pair of metal bracers");
		setDisplayText("a pair of metal bracers lie here.");
		setDescription("Good and solid protection for your arms.");
		properWornBitmap=Wearable.WORN_LEFT_WRIST | Wearable.WORN_RIGHT_WRIST | Wearable.WORN_ARMS;
		wornLogicalAnd=true;
		basePhyStats().setArmor(4);
		basePhyStats().setWeight(10);
		basePhyStats().setAbility(0);
		baseGoldValue=10;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
	}

}
