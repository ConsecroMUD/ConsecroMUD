package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class SwampGrid extends StdGrid
{
	@Override public String ID(){return "SwampGrid";}
	public SwampGrid()
	{
		super();
		name="the swamp";
		basePhyStats.setWeight(3);
		recoverPhyStats();
		climask=Places.CLIMASK_WET;
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_SWAMP;}

	@Override public String getGridChildLocaleID(){return "Swamp";}
	@Override public List<Integer> resourceChoices(){return Swamp.roomResources;}
}
