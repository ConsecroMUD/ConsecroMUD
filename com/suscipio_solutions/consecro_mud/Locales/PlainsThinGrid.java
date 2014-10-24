package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;


public class PlainsThinGrid extends StdThinGrid
{
	@Override public String ID(){return "PlainsThinGrid";}
	public PlainsThinGrid()
	{
		super();
		basePhyStats.setWeight(2);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_PLAINS;}

	@Override
	public CMObject newInstance()
	{
		if(!CMSecurity.isDisabled(CMSecurity.DisFlag.THINGRIDS))
			return super.newInstance();
		return new PlainsGrid().newInstance();
	}
	@Override public String getGridChildLocaleID(){return "Plains";}
	@Override public List<Integer> resourceChoices(){return Plains.roomResources;}
}
