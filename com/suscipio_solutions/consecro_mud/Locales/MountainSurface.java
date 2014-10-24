package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;


public class MountainSurface extends ClimbableSurface
{
	@Override public String ID(){return "MountainSurface";}
	public MountainSurface()
	{
		super();
		basePhyStats.setWeight(6);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_MOUNTAINS;}

	@Override public List<Integer> resourceChoices(){return Mountains.roomResources;}
}
