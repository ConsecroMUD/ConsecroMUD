package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;


public class RoadGrid extends StdGrid
{
	@Override public String ID(){return "RoadGrid";}
	public RoadGrid()
	{
		super();
		name="a road";
		basePhyStats.setWeight(1);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_PLAINS;}

	@Override public String getGridChildLocaleID(){return "Road";}
	@Override public List<Integer> resourceChoices(){return Road.roomResources;}
}
