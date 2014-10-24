package com.suscipio_solutions.consecro_mud.Items.Basic;


public class GenEmptyDrink extends GenDrink
{
	@Override public String ID(){    return "GenEmptyDrink";}


	public GenEmptyDrink()
	{
		super();
		this.amountOfLiquidRemaining=0;
	}
}
