package com.suscipio_solutions.consecro_mud.Locales;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class FrozenPlains extends Plains
{
	@Override public String ID(){return "FrozenPlains";}
	public FrozenPlains()
	{
		super();
		recoverPhyStats();
		climask=Places.CLIMASK_COLD;
	}

	public static final Integer[] resourceList={
		Integer.valueOf(RawMaterial.RESOURCE_FUR)};
	public static final List<Integer> roomResources=new Vector<Integer>(Arrays.asList(resourceList));
	@Override public List<Integer> resourceChoices(){return Plains.roomResources;}
}
