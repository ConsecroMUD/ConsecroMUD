package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class Pan extends StdDrink
{
	@Override public String ID(){	return "Pan";}
	public Pan()
	{
		super();
		setName("a pan");
		setDisplayText("an iron pan sits here.");
		setDescription("A sturdy iron pan for cooking in.");
		capacity=25;
		baseGoldValue=5;
		setMaterial(RawMaterial.RESOURCE_IRON);
		basePhyStats().setWeight(5);
		recoverPhyStats();
	}



}
