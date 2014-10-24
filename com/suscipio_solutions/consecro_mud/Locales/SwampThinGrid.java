package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class SwampThinGrid extends StdThinGrid
{
	@Override public String ID(){return "SwampThinGrid";}
	public SwampThinGrid()
	{
		super();
		name="the swamp";
		basePhyStats.setWeight(3);
		recoverPhyStats();
		climask=Places.CLIMASK_WET;
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_SWAMP;}

	@Override
	public CMObject newInstance()
	{
		if(!CMSecurity.isDisabled(CMSecurity.DisFlag.THINGRIDS))
			return super.newInstance();
		return new SwampGrid().newInstance();
	}
	@Override public String getGridChildLocaleID(){return "Swamp";}
	@Override public List<Integer> resourceChoices(){return Swamp.roomResources;}
}
