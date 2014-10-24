package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


public class GenTable extends GenRideable
{
	@Override public String ID(){	return "GenTable";}

	public GenTable()
	{
		super();

		readableText = "";

		setName("a generic table");
		basePhyStats.setWeight(250);
		setDisplayText("a generic table is here.");
		setDescription("");
		material=RawMaterial.RESOURCE_OAK;
		baseGoldValue=5;
		basePhyStats().setLevel(1);
		setRiderCapacity(4);
		setRideBasis(Rideable.RIDEABLE_TABLE);
		recoverPhyStats();
	}

}
