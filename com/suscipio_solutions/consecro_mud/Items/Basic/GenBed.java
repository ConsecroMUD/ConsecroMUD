package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


public class GenBed extends GenRideable
{
	@Override public String ID(){	return "GenBed";}

	public GenBed()
	{
		super();

		readableText = "";

		setName("a generic bed");
		basePhyStats.setWeight(150);
		setDisplayText("a generic bed sits here.");
		setDescription("");
		baseGoldValue=5;
		basePhyStats().setLevel(1);
		setMaterial(RawMaterial.RESOURCE_COTTON);
		setRiderCapacity(2);
		setRideBasis(Rideable.RIDEABLE_SLEEP);
		recoverPhyStats();
	}

}
