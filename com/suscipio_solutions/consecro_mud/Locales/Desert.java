package com.suscipio_solutions.consecro_mud.Locales;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class Desert extends StdRoom
{
	@Override public String ID(){return "Desert";}
	public Desert()
	{
		super();
		name="the desert";
		basePhyStats.setWeight(2);
		recoverPhyStats();
		climask=Places.CLIMASK_HOT|CLIMASK_DRY;
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_DESERT;}
	@Override protected int baseThirst(){return 4;}

	public static final Integer[] resourceList={
		Integer.valueOf(RawMaterial.RESOURCE_CACTUS),
		Integer.valueOf(RawMaterial.RESOURCE_SAND),
		Integer.valueOf(RawMaterial.RESOURCE_LAMPOIL),
		Integer.valueOf(RawMaterial.RESOURCE_PEPPERS),
		Integer.valueOf(RawMaterial.RESOURCE_SCALES),
		Integer.valueOf(RawMaterial.RESOURCE_DATES)
	};
	public static final List<Integer> roomResources=new Vector<Integer>(Arrays.asList(resourceList));
	@Override public List<Integer> resourceChoices(){return Desert.roomResources;}
}
