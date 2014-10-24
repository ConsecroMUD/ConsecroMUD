package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;


public class HillsThinGrid extends StdThinGrid
{
	@Override public String ID(){return "HillsThinGrid";}
	public HillsThinGrid()
	{
		super();
		name="the hills";
		basePhyStats.setWeight(3);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_HILLS;}

	@Override
	public CMObject newInstance()
	{
		if(!CMSecurity.isDisabled(CMSecurity.DisFlag.THINGRIDS))
			return super.newInstance();
		return new HillsGrid().newInstance();
	}
	@Override public String getGridChildLocaleID(){return "Hills";}
	@Override public List<Integer> resourceChoices(){return Hills.roomResources;}
}
