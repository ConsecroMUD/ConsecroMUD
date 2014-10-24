package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class WetCaveRoom extends CaveRoom
{
	@Override public String ID(){return "WetCaveRoom";}
	public WetCaveRoom()
	{
		super();
		recoverPhyStats();
		climask=Places.CLIMASK_WET;
	}
	@Override public int maxRange(){return 5;}
	@Override public List<Integer> resourceChoices(){return CaveRoom.roomResources;}
}
