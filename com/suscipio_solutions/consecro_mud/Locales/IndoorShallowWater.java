package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class IndoorShallowWater extends ShallowWater implements Drink
{
	@Override public String ID(){return "IndoorShallowWater";}
	public IndoorShallowWater()
	{
		super();
		name="the water";
		recoverPhyStats();
		climask=Places.CLIMASK_WET;
	}
	@Override public int domainType(){return Room.DOMAIN_INDOORS_WATERSURFACE;}
	@Override protected int baseThirst(){return 0;}

	@Override public List<Integer> resourceChoices(){return CaveRoom.roomResources;}
}
