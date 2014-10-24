package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class GenShoes extends GenArmor
{
	@Override public String ID(){	return "GenShoes";}
	public GenShoes()
	{
		super();

		setName("a pair of shoes");
		setDisplayText("a pair of shoes lies here");
		setDescription("a well tailored pair of walking shoes.");
		properWornBitmap=Wearable.WORN_FEET;
		wornLogicalAnd=false;
		basePhyStats().setArmor(1);
		basePhyStats().setWeight(1);
		basePhyStats().setAbility(0);
		baseGoldValue=1;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_LEATHER;
	}

}
