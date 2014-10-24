package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;


public class RoadThinGrid extends StdThinGrid
{
	@Override public String ID(){return "RoadThinGrid";}
	public RoadThinGrid()
	{
		super();
		name="a road";
		basePhyStats.setWeight(1);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_PLAINS;}

	@Override
	public CMObject newInstance()
	{
		if(!CMSecurity.isDisabled(CMSecurity.DisFlag.THINGRIDS))
			return super.newInstance();
		return new RoadGrid().newInstance();
	}
	@Override public String getGridChildLocaleID(){return "Road";}
	@Override public List<Integer> resourceChoices(){return Road.roomResources;}
}
