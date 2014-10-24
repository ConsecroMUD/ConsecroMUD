package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;


public class GenWater extends GenDrink
{
	@Override public String ID(){	return "GenWater";}

	public GenWater()
	{
		super();
		readableText = "";

		setName("a generic puddle of water");
		basePhyStats.setWeight(2);
		setDisplayText("a generic puddle of water sits here.");
		setDescription("");
		baseGoldValue=0;
		capacity=0;
		amountOfThirstQuenched=250;
		amountOfLiquidHeld=10000;
		amountOfLiquidRemaining=10000;
		setMaterial(RawMaterial.RESOURCE_FRESHWATER);
		basePhyStats().setSensesMask(PhyStats.SENSE_ITEMNOTGET);
		recoverPhyStats();
	}


}
