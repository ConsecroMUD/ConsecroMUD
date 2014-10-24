package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class GreatThinLake extends StdThinGrid
{
	@Override public String ID(){return "GreatThinLake";}
	public GreatThinLake()
	{
		super();
		name="the lake";
		basePhyStats.setWeight(2);
		recoverPhyStats();
		climask=Places.CLIMASK_WET;
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_WATERSURFACE;}

	@Override
	public CMObject newInstance()
	{
		if(!CMSecurity.isDisabled(CMSecurity.DisFlag.THINGRIDS))
			return super.newInstance();
		return new GreatLake().newInstance();
	}
	@Override public String getGridChildLocaleID(){return "WaterSurface";}
	@Override public List<Integer> resourceChoices(){return UnderWater.roomResources;}
}
