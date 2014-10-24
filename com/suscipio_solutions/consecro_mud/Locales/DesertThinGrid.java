package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class DesertThinGrid extends StdThinGrid
{
	@Override public String ID(){return "DesertThinGrid";}
	public DesertThinGrid()
	{
		super();
		name="the desert";
		basePhyStats.setWeight(2);
		recoverPhyStats();
		climask=Places.CLIMASK_HOT|CLIMASK_DRY;
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_DESERT;}

	@Override
	public CMObject newInstance()
	{
		if(!CMSecurity.isDisabled(CMSecurity.DisFlag.THINGRIDS))
			return super.newInstance();
		return new DesertGrid().newInstance();
	}

	@Override public String getGridChildLocaleID(){return "Desert";}
	@Override public List<Integer> resourceChoices(){return Desert.roomResources;}
}
