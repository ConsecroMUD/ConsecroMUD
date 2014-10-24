package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class DesertMaze extends StdMaze
{
	@Override public String ID(){return "DesertMaze";}
	public DesertMaze()
	{
		super();
		basePhyStats.setWeight(2);
		recoverPhyStats();
		climask=Places.CLIMASK_HOT|CLIMASK_DRY;
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_DESERT;}
	@Override protected int baseThirst(){return 4;}

	@Override public String getGridChildLocaleID(){return "Desert";}
	@Override public List<Integer> resourceChoices(){return Desert.roomResources;}
}
