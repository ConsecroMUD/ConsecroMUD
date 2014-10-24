package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;


public class WoodsThinGrid extends StdThinGrid
{
	@Override public String ID(){return "WoodsThinGrid";}
	public WoodsThinGrid()
	{
		super();
		basePhyStats.setWeight(3);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_WOODS;}

	@Override public String getGridChildLocaleID(){return "Woods";}
	@Override public List<Integer> resourceChoices(){return Woods.roomResources;}
	@Override
	public CMObject newInstance()
	{
		if(!CMSecurity.isDisabled(CMSecurity.DisFlag.THINGRIDS))
			return super.newInstance();
		return new WoodsGrid().newInstance();
	}
}
