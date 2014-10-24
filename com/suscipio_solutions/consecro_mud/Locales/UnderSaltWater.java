package com.suscipio_solutions.consecro_mud.Locales;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;

public class UnderSaltWater extends UnderWater
{
	@Override public String ID(){return "UnderSaltWater";}
	public UnderSaltWater()
	{
		super();
		climask=Places.CLIMASK_WET;
		atmosphere=RawMaterial.RESOURCE_SALTWATER;
	}


	@Override public int liquidType(){return RawMaterial.RESOURCE_SALTWATER;}
	public static final Integer[] resourceList={
		Integer.valueOf(RawMaterial.RESOURCE_SEAWEED),
		Integer.valueOf(RawMaterial.RESOURCE_FISH),
		Integer.valueOf(RawMaterial.RESOURCE_TUNA),
		Integer.valueOf(RawMaterial.RESOURCE_SHRIMP),
		Integer.valueOf(RawMaterial.RESOURCE_SAND),
		Integer.valueOf(RawMaterial.RESOURCE_CLAY),
		Integer.valueOf(RawMaterial.RESOURCE_PEARL),
		Integer.valueOf(RawMaterial.RESOURCE_LIMESTONE)};
	public static final List<Integer> roomResources=new Vector<Integer>(Arrays.asList(resourceList));
	@Override public List<Integer> resourceChoices(){return UnderWater.roomResources;}
}
