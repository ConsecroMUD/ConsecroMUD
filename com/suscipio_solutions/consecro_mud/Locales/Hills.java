package com.suscipio_solutions.consecro_mud.Locales;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;


public class Hills extends StdRoom
{
	@Override public String ID(){return "Hills";}
	public Hills()
	{
		super();
		name="the hills";
		basePhyStats.setWeight(3);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_HILLS;}

	public static final Integer[] resourceList={
		Integer.valueOf(RawMaterial.RESOURCE_GRAPES),
		Integer.valueOf(RawMaterial.RESOURCE_BERRIES),
		Integer.valueOf(RawMaterial.RESOURCE_BLUEBERRIES),
		Integer.valueOf(RawMaterial.RESOURCE_BLACKBERRIES),
		Integer.valueOf(RawMaterial.RESOURCE_STRAWBERRIES),
		Integer.valueOf(RawMaterial.RESOURCE_RASPBERRIES),
		Integer.valueOf(RawMaterial.RESOURCE_BOYSENBERRIES),
		Integer.valueOf(RawMaterial.RESOURCE_GREENS),
		Integer.valueOf(RawMaterial.RESOURCE_OLIVES),
		Integer.valueOf(RawMaterial.RESOURCE_BEANS),
		Integer.valueOf(RawMaterial.RESOURCE_RICE),
		Integer.valueOf(RawMaterial.RESOURCE_LEATHER),
		Integer.valueOf(RawMaterial.RESOURCE_FEATHERS),
		Integer.valueOf(RawMaterial.RESOURCE_MESQUITE),
		Integer.valueOf(RawMaterial.RESOURCE_EGGS),
		Integer.valueOf(RawMaterial.RESOURCE_HERBS),
		Integer.valueOf(RawMaterial.RESOURCE_POTATOES)
	};
	public static final List<Integer> roomResources=new Vector<Integer>(Arrays.asList(resourceList));
	@Override public List<Integer> resourceChoices(){return Hills.roomResources;}
}
