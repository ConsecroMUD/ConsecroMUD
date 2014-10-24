package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


public class Boat extends StdRideable
{
	@Override public String ID(){	return "Boat";}
	public Boat()
	{
		super();
		setName("a boat");
		setDisplayText("a boat is docked here.");
		setDescription("Looks like a boat");
		rideBasis=Rideable.RIDEABLE_WATER;
		material=RawMaterial.RESOURCE_OAK;
	}

}
