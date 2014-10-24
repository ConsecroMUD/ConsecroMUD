package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;


public class PlainsGrid extends StdGrid
{
	@Override public String ID(){return "PlainsGrid";}
	public PlainsGrid()
	{
		super();
		basePhyStats.setWeight(2);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_PLAINS;}

	@Override public String getGridChildLocaleID(){return "Plains";}
	@Override public List<Integer> resourceChoices(){return Plains.roomResources;}
}
