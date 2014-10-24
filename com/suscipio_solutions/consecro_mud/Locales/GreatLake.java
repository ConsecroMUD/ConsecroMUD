package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class GreatLake extends StdGrid
{
	@Override public String ID(){return "GreatLake";}
	public GreatLake()
	{
		super();
		name="the lake";
		basePhyStats.setWeight(2);
		recoverPhyStats();
		climask=Places.CLIMASK_WET;
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_WATERSURFACE;}

	@Override public String getGridChildLocaleID(){return "WaterSurface";}
	@Override public List<Integer> resourceChoices(){return UnderWater.roomResources;}
}
