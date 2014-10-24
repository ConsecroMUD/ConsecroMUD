package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class WetCaveMaze extends StdMaze
{
	@Override public String ID(){return "WetCaveMaze";}
	public WetCaveMaze()
	{
		super();
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_DARK);
		recoverPhyStats();
		climask=Places.CLIMASK_WET;
	}
	@Override public int domainType(){return Room.DOMAIN_INDOORS_CAVE;}

	@Override public String getGridChildLocaleID(){return "WetCaveRoom";}
	@Override public int maxRange(){return 5;}
	@Override public List<Integer> resourceChoices(){return CaveRoom.roomResources;}
}
