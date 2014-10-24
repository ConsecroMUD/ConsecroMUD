package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;


public class WoodsGrid extends StdGrid
{
	@Override public String ID(){return "WoodsGrid";}
	public WoodsGrid()
	{
		super();
		basePhyStats.setWeight(3);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_WOODS;}

	@Override public String getGridChildLocaleID(){return "Woods";}
	@Override public List<Integer> resourceChoices(){return Woods.roomResources;}
}
