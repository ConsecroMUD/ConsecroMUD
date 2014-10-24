package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class FrozenMountains extends Mountains
{
	@Override public String ID(){return "FrozenMountains";}
	public FrozenMountains()
	{
		super();
		recoverPhyStats();
		climask=Places.CLIMASK_COLD;
	}

	@Override public List<Integer> resourceChoices(){return Mountains.roomResources;}
}
