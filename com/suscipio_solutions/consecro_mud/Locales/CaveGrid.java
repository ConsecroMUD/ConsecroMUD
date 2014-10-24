package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class CaveGrid extends StdGrid
{
	@Override public String ID(){return "CaveGrid";}
	public CaveGrid()
	{
		super();
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_DARK);
		recoverPhyStats();
		climask=Places.CLIMASK_NORMAL;
	}
	@Override public int domainType(){return Room.DOMAIN_INDOORS_CAVE;}

	@Override public int maxRange(){return 5;}
	@Override public String getGridChildLocaleID(){return "CaveRoom";}
	@Override public List<Integer> resourceChoices(){return CaveRoom.roomResources;}
}
