package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class DesertGrid extends StdGrid
{
	@Override public String ID(){return "DesertGrid";}
	public DesertGrid()
	{
		super();
		name="the desert";
		basePhyStats.setWeight(2);
		recoverPhyStats();
		climask=Places.CLIMASK_HOT|CLIMASK_DRY;
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_DESERT;}

	@Override public String getGridChildLocaleID(){return "Desert";}
	@Override public List<Integer> resourceChoices(){return Desert.roomResources;}
}
