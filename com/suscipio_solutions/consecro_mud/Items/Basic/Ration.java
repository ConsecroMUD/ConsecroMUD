package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;


public class Ration extends StdFood
{
	@Override public String ID(){	return "Ration";}
	public Ration()
	{
		super();
		setName("a ration pack");
		basePhyStats.setWeight(10);
		amountOfNourishment=500;
		setDisplayText("a standard ration pack sits here.");
		setDescription("Bits of salt dried meat, dried fruit, and hard bread.");
		baseGoldValue=15;
		setMaterial(RawMaterial.RESOURCE_MEAT);
		recoverPhyStats();
	}


}
