package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


public class GenChair extends GenRideable
{
	@Override public String ID(){	return "GenChair";}

	public GenChair()
	{
		super();

		readableText = "";

		setName("a generic chair");
		basePhyStats.setWeight(150);
		setDisplayText("a generic chair is here.");
		setDescription("");
		material=RawMaterial.RESOURCE_OAK;
		baseGoldValue=5;
		basePhyStats().setLevel(1);
		setRiderCapacity(1);
		setRideBasis(Rideable.RIDEABLE_SIT);
		recoverPhyStats();
	}
}
