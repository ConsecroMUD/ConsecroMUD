package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;


public class GenFountain extends GenWater
{
	@Override public String ID(){	return "GenFountain";}
	public GenFountain()
	{
		super();
		setName("a fountain");
		amountOfThirstQuenched=250;
		amountOfLiquidHeld=999999;
		amountOfLiquidRemaining=999999;
		basePhyStats().setWeight(5);
		capacity=0;
		setDisplayText("a little fountain flows here.");
		setDescription("The water looks pure and clean.");
		baseGoldValue=10;
		basePhyStats().setSensesMask(PhyStats.SENSE_ITEMNOTGET);
		material=RawMaterial.RESOURCE_FRESHWATER;
		recoverPhyStats();
	}


}
