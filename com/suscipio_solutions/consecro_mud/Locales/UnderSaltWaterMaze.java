package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class UnderSaltWaterMaze extends UnderWaterMaze
{
	@Override public String ID(){return "UnderSaltWaterMaze";}
	public UnderSaltWaterMaze()
	{
		super();
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_SWIMMING);
		basePhyStats.setWeight(3);
		recoverPhyStats();
		climask=Places.CLIMASK_WET;
		atmosphere=RawMaterial.RESOURCE_SALTWATER;
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_UNDERWATER;}
	@Override protected int baseThirst(){return 0;}


	@Override public String getGridChildLocaleID(){return "UnderSaltWater";}

	@Override public List<Integer> resourceChoices(){return UnderSaltWater.roomResources;}
}
