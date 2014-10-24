package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class LeatherCap extends StdArmor
{
	@Override public String ID(){	return "LeatherCap";}
	public LeatherCap()
	{
		super();

		setName("a leather cap");
		setDisplayText("a round leather cap sits here.");
		setDescription("It looks like its made of cured leather hide, with metal bindings.");
		properWornBitmap=Wearable.WORN_HEAD;
		wornLogicalAnd=false;
		basePhyStats().setArmor(4);
		basePhyStats().setWeight(1);
		basePhyStats().setAbility(0);
		baseGoldValue=5;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_LEATHER;
	}

}
