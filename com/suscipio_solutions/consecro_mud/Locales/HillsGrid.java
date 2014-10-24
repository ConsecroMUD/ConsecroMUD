package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;


public class HillsGrid extends StdGrid
{
	@Override public String ID(){return "HillsGrid";}
	public HillsGrid()
	{
		super();
		name="the hills";
		basePhyStats.setWeight(3);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_HILLS;}

	@Override public String getGridChildLocaleID(){return "Hills";}
	@Override public List<Integer> resourceChoices(){return Hills.roomResources;}
}
