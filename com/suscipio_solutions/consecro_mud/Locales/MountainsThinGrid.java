package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;


public class MountainsThinGrid extends StdThinGrid
{
	@Override public String ID(){return "MountainsThinGrid";}
	public MountainsThinGrid()
	{
		super();
		name="the mountains";
		basePhyStats.setWeight(5);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_MOUNTAINS;}

	@Override
	public CMObject newInstance()
	{
		if(!CMSecurity.isDisabled(CMSecurity.DisFlag.THINGRIDS))
			return super.newInstance();
		return new MountainsGrid().newInstance();
	}
	@Override public String getGridChildLocaleID(){return "Mountains";}
	@Override public List<Integer> resourceChoices(){return Mountains.roomResources;}
}
