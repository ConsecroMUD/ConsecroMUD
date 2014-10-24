package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class JungleThinGrid extends StdThinGrid
{
	@Override public String ID(){return "JungleThinGrid";}
	public JungleThinGrid()
	{
		super();
		name="the jungle";
		basePhyStats.setWeight(3);
		recoverPhyStats();
		climask=Places.CLIMASK_WET|CLIMASK_HOT;
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_JUNGLE;}

	@Override
	public CMObject newInstance()
	{
		if(!CMSecurity.isDisabled(CMSecurity.DisFlag.THINGRIDS))
			return super.newInstance();
		return new JungleGrid().newInstance();
	}
	@Override public String getGridChildLocaleID(){return "Jungle";}
	@Override public List<Integer> resourceChoices(){return Jungle.roomResources;}
}
