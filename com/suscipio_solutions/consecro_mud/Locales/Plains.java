package com.suscipio_solutions.consecro_mud.Locales;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;


public class Plains extends StdRoom
{
	@Override public String ID(){return "Plains";}
	public Plains()
	{
		super();
		name="the grass";
		basePhyStats.setWeight(2);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_PLAINS;}

	public static final Integer[] resourceList={
		Integer.valueOf(RawMaterial.RESOURCE_WHEAT),
		Integer.valueOf(RawMaterial.RESOURCE_HOPS),
		Integer.valueOf(RawMaterial.RESOURCE_BARLEY),
		Integer.valueOf(RawMaterial.RESOURCE_CORN),
		Integer.valueOf(RawMaterial.RESOURCE_RICE),
		Integer.valueOf(RawMaterial.RESOURCE_SMURFBERRIES),
		Integer.valueOf(RawMaterial.RESOURCE_GREENS),
		Integer.valueOf(RawMaterial.RESOURCE_CARROTS),
		Integer.valueOf(RawMaterial.RESOURCE_TOMATOES),
		Integer.valueOf(RawMaterial.RESOURCE_BEANS),
		Integer.valueOf(RawMaterial.RESOURCE_ONIONS),
		Integer.valueOf(RawMaterial.RESOURCE_GARLIC),
		Integer.valueOf(RawMaterial.RESOURCE_FLINT),
		Integer.valueOf(RawMaterial.RESOURCE_COTTON),
		Integer.valueOf(RawMaterial.RESOURCE_MEAT),
		Integer.valueOf(RawMaterial.RESOURCE_HERBS),
		Integer.valueOf(RawMaterial.RESOURCE_EGGS),
		Integer.valueOf(RawMaterial.RESOURCE_BEEF),
		Integer.valueOf(RawMaterial.RESOURCE_HIDE),
		Integer.valueOf(RawMaterial.RESOURCE_FUR),
		Integer.valueOf(RawMaterial.RESOURCE_HONEY),
		Integer.valueOf(RawMaterial.RESOURCE_FEATHERS),
		Integer.valueOf(RawMaterial.RESOURCE_LEATHER),
		Integer.valueOf(RawMaterial.RESOURCE_WOOL)};
	public static final List<Integer> roomResources=new Vector<Integer>(Arrays.asList(resourceList));
	@Override public List<Integer> resourceChoices(){return Plains.roomResources;}
}
