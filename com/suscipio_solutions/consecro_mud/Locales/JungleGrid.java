package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class JungleGrid extends StdGrid
{
	@Override public String ID(){return "JungleGrid";}
	public JungleGrid()
	{
		super();
		name="the jungle";
		basePhyStats.setWeight(3);
		recoverPhyStats();
		climask=Places.CLIMASK_WET|CLIMASK_HOT;
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_JUNGLE;}

	@Override public String getGridChildLocaleID(){return "Jungle";}
	@Override public List<Integer> resourceChoices(){return Jungle.roomResources;}
}
