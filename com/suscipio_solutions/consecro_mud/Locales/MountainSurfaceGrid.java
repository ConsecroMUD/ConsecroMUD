package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;


public class MountainSurfaceGrid extends StdGrid
{
	@Override public String ID(){return "MountainSurfaceGrid";}
	public MountainSurfaceGrid()
	{
		super();
		name="the mountains";
		basePhyStats.setWeight(5);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_MOUNTAINS;}

	@Override public String getGridChildLocaleID(){return "MountainSurface";}
	@Override public List<Integer> resourceChoices(){return Mountains.roomResources;}
}
