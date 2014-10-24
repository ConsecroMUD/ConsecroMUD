package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


public class GenBoat extends GenRideable
{
	@Override public String ID(){	return "GenBoat";}

	public GenBoat()
	{
		super();
		setName("a boat");
		setDisplayText("a boat is docked here.");
		setDescription("Looks like a boat");
		rideBasis=Rideable.RIDEABLE_WATER;
		material=RawMaterial.RESOURCE_OAK;
		recoverPhyStats();
	}

}
