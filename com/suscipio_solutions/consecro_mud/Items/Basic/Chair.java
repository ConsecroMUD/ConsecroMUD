package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


public class Chair extends StdRideable
{
	@Override public String ID(){	return "Chair";}
	protected String	readableText="";
	public Chair()
	{
		super();
		setName("a chair");
		basePhyStats.setWeight(150);
		setDisplayText("a chair is here.");
		setDescription("Looks like a nice comfortable wooden chair");
		material=RawMaterial.RESOURCE_OAK;
		baseGoldValue=5;
		basePhyStats().setLevel(1);
		setRideBasis(Rideable.RIDEABLE_SIT);
		setRiderCapacity(1);
		recoverPhyStats();
	}

}
