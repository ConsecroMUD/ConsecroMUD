package com.suscipio_solutions.consecro_mud.Locales;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;


public class Mountains extends StdRoom
{
	@Override public String ID(){return "Mountains";}
	public Mountains()
	{
		super();
		name="the mountain";
		basePhyStats.setWeight(5);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_MOUNTAINS;}

	public static final Integer[] resourceList={
		Integer.valueOf(RawMaterial.RESOURCE_STONE),
		Integer.valueOf(RawMaterial.RESOURCE_IRON),
		Integer.valueOf(RawMaterial.RESOURCE_ALABASTER),
		Integer.valueOf(RawMaterial.RESOURCE_LEAD),
		Integer.valueOf(RawMaterial.RESOURCE_SILVER),
		Integer.valueOf(RawMaterial.RESOURCE_COPPER),
		Integer.valueOf(RawMaterial.RESOURCE_TIN),
		Integer.valueOf(RawMaterial.RESOURCE_AMETHYST),
		Integer.valueOf(RawMaterial.RESOURCE_GARNET),
		Integer.valueOf(RawMaterial.RESOURCE_AMBER),
		Integer.valueOf(RawMaterial.RESOURCE_HERBS),
		Integer.valueOf(RawMaterial.RESOURCE_OPAL),
		Integer.valueOf(RawMaterial.RESOURCE_TOPAZ),
		Integer.valueOf(RawMaterial.RESOURCE_BASALT),
		Integer.valueOf(RawMaterial.RESOURCE_SHALE),
		Integer.valueOf(RawMaterial.RESOURCE_PUMICE),
		Integer.valueOf(RawMaterial.RESOURCE_SANDSTONE),
		Integer.valueOf(RawMaterial.RESOURCE_SOAPSTONE),
		Integer.valueOf(RawMaterial.RESOURCE_AQUAMARINE),
		Integer.valueOf(RawMaterial.RESOURCE_CRYSOBERYL),
		Integer.valueOf(RawMaterial.RESOURCE_ONYX),
		Integer.valueOf(RawMaterial.RESOURCE_TURQUOISE),
		Integer.valueOf(RawMaterial.RESOURCE_DIAMOND),
		Integer.valueOf(RawMaterial.RESOURCE_CRYSTAL),
		Integer.valueOf(RawMaterial.RESOURCE_QUARTZ),
		Integer.valueOf(RawMaterial.RESOURCE_PLATINUM)};
	public static final List<Integer> roomResources=new Vector<Integer>(Arrays.asList(resourceList));
	@Override public List<Integer> resourceChoices(){return Mountains.roomResources;}
}
