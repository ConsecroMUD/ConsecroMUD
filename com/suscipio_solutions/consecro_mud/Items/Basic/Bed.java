package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


public class Bed extends GenRideable
{
	@Override public String ID(){	return "Bed";}

	public Bed()
	{
		super();

		readableText = "";

		setName("a bed");
		basePhyStats.setWeight(150);
		setDisplayText("a bed is here.");
		setDescription("Looks like a nice comfortable bed");
		baseGoldValue=5;
		basePhyStats().setLevel(1);
		setMaterial(RawMaterial.RESOURCE_COTTON);
		setRideBasis(Rideable.RIDEABLE_SLEEP);
		setRiderCapacity(2);
		recoverPhyStats();
	}

}
