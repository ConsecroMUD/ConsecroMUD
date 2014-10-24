package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class Pants extends StdArmor
{
	@Override public String ID(){	return "Pants";}
	public Pants()
	{
		super();

		setName("a pair of pants");
		setDisplayText("a pair of pants lies here");
		setDescription("a well tailored pair of travelers pants.");
		properWornBitmap=Wearable.WORN_LEGS;
		wornLogicalAnd=false;
		basePhyStats().setArmor(1);
		basePhyStats().setWeight(1);
		basePhyStats().setAbility(0);
		baseGoldValue=1;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_COTTON;
	}

}
