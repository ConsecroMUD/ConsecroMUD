package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;


public class Spring extends StdDrink
{
	@Override public String ID(){	return "Spring";}
	public Spring()
	{
		super();
		setName("a spring");
		amountOfThirstQuenched=250;
		amountOfLiquidHeld=999999;
		amountOfLiquidRemaining=999999;
		basePhyStats().setWeight(5);
		capacity=0;
		setDisplayText("a little magical spring flows here.");
		setDescription("The spring is coming magically from the ground.  The water looks pure and clean.");
		baseGoldValue=10;
		basePhyStats().setSensesMask(PhyStats.SENSE_ITEMNOTGET);
		material=RawMaterial.RESOURCE_FRESHWATER;
		recoverPhyStats();
	}


}
