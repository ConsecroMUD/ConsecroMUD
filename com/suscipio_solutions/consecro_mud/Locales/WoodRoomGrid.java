package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class WoodRoomGrid extends StdGrid
{
	@Override public String ID(){return "WoodRoomGrid";}
	public WoodRoomGrid()
	{
		super();
		basePhyStats.setWeight(1);
		recoverPhyStats();
		climask=Places.CLIMASK_NORMAL;
	}
	@Override public int domainType(){return Room.DOMAIN_INDOORS_WOOD;}

	@Override public String getGridChildLocaleID(){return "WoodRoom";}
}
