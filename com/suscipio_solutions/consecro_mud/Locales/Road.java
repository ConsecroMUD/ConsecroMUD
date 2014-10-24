package com.suscipio_solutions.consecro_mud.Locales;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;


public class Road extends StdRoom
{
	@Override public String ID(){return "Road";}
	public Road()
	{
		super();
		name="a road";
		basePhyStats.setWeight(1);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_PLAINS;}

	public static final Integer[] resourceList={
		Integer.valueOf(RawMaterial.RESOURCE_STONE),
		Integer.valueOf(RawMaterial.RESOURCE_FEATHERS),
		Integer.valueOf(RawMaterial.RESOURCE_SCALES),
		Integer.valueOf(RawMaterial.RESOURCE_SAND),
		Integer.valueOf(RawMaterial.RESOURCE_CLAY),
	};
	public static final List<Integer> roomResources=new Vector<Integer>(Arrays.asList(resourceList));
	@Override public List<Integer> resourceChoices(){return Road.roomResources;}
}
