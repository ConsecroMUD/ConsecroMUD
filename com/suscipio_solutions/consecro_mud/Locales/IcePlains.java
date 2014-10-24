package com.suscipio_solutions.consecro_mud.Locales;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class IcePlains extends StdRoom
{
	@Override public String ID(){return "IcePlains";}
	public IcePlains()
	{
		super();
		name="the snow";
		basePhyStats.setWeight(2);
		recoverPhyStats();
		climask=Places.CLIMASK_COLD|Places.CLIMASK_WET;
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_PLAINS;}

	public static final Integer[] resourceList={
		Integer.valueOf(RawMaterial.RESOURCE_ELM),
		Integer.valueOf(RawMaterial.RESOURCE_MAPLE),
		Integer.valueOf(RawMaterial.RESOURCE_BERRIES),
		Integer.valueOf(RawMaterial.RESOURCE_CARROTS),
		Integer.valueOf(RawMaterial.RESOURCE_GREENS),
		Integer.valueOf(RawMaterial.RESOURCE_ONIONS),
		Integer.valueOf(RawMaterial.RESOURCE_FLINT),
		Integer.valueOf(RawMaterial.RESOURCE_COTTON),
		Integer.valueOf(RawMaterial.RESOURCE_MEAT),
		Integer.valueOf(RawMaterial.RESOURCE_EGGS),
		Integer.valueOf(RawMaterial.RESOURCE_BEEF),
		Integer.valueOf(RawMaterial.RESOURCE_HIDE),
		Integer.valueOf(RawMaterial.RESOURCE_FUR),
		Integer.valueOf(RawMaterial.RESOURCE_FEATHERS),
		Integer.valueOf(RawMaterial.RESOURCE_LEATHER),
		Integer.valueOf(RawMaterial.RESOURCE_WOOL)};
	public static final List<Integer> roomResources=new Vector<Integer>(Arrays.asList(resourceList));
	@Override public List<Integer> resourceChoices(){return Plains.roomResources;}
}
