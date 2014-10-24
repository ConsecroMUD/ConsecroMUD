package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;


public class Wineskin extends StdDrink
{
	@Override public String ID(){	return "Wineskin";}
	public Wineskin()
	{
		super();

		setName("a wineskin");
		amountOfThirstQuenched=200;
		amountOfLiquidHeld=1000;
		amountOfLiquidRemaining=1000;
		basePhyStats.setWeight(10);
		capacity=5;
		setDisplayText("a tough little wineskin sits here.");
		setDescription("Looks like it could hold quite a bit of drink.");
		baseGoldValue=10;
		material=RawMaterial.RESOURCE_LEATHER;
		recoverPhyStats();
	}



}
