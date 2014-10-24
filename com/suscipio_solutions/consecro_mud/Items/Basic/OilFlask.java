package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;


public class OilFlask extends StdDrink
{
	@Override public String ID(){	return "OilFlask";}


	public OilFlask()
	{
		super();
		setName("an oil flask");
		basePhyStats.setWeight(3);
		capacity=0;
		setMaterial(RawMaterial.RESOURCE_GLASS);
		setDisplayText("an oil flask sits here.");
		setDescription("A small glass flask containing lamp oil, with a lid.");
		baseGoldValue=5;
		amountOfLiquidHeld=5;
		disappearsAfterDrinking=true;
		amountOfLiquidRemaining=5;
		liquidType=RawMaterial.RESOURCE_LAMPOIL;
		recoverPhyStats();
	}


}
