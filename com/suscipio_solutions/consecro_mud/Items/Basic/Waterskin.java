package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;


public class Waterskin extends StdDrink
{
	@Override public String ID(){	return "Waterskin";}
	public Waterskin()
	{
		super();
		setName("a waterskin");
		amountOfThirstQuenched=200;
		amountOfLiquidHeld=1000;
		amountOfLiquidRemaining=1000;
		basePhyStats.setWeight(10);
		capacity=15;
		setDisplayText("a tough little waterskin sits here.");
		setDescription("Looks like it could hold quite a bit of drink.");
		baseGoldValue=10;
		material=RawMaterial.RESOURCE_LEATHER;
		recoverPhyStats();
	}



}
