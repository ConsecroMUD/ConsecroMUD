package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class Pot extends StdDrink
{
	@Override public String ID(){	return "Pot";}
	public Pot()
	{
		super();
		setName("a pot");
		setDisplayText("a cooking pot sits here.");
		setDescription("A sturdy iron pot for cooking in.");
		capacity=25;
		baseGoldValue=5;
		setLiquidHeld(20);
		setThirstQuenched(1);
		setLiquidRemaining(0);
		setLiquidType(RawMaterial.RESOURCE_FRESHWATER);
		setMaterial(RawMaterial.RESOURCE_IRON);
		basePhyStats().setWeight(5);
		recoverPhyStats();
	}



}
