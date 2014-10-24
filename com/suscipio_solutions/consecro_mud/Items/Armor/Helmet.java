package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class Helmet extends StdArmor
{
	@Override public String ID(){	return "Helmet";}
	public Helmet()
	{
		super();

		setName("a helmet");
		setDisplayText("a helmet sits here.");
		setDescription("This is fairly solid looking helmet.");
		properWornBitmap=Wearable.WORN_HEAD;
		wornLogicalAnd=false;
		basePhyStats().setArmor(10);
		basePhyStats().setWeight(10);
		basePhyStats().setAbility(0);
		baseGoldValue=16;
		material=RawMaterial.RESOURCE_IRON;
		recoverPhyStats();
	}

}
