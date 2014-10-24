package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class HotRoom extends StdRoom
{
	@Override public String ID(){return "HotRoom";}
	public HotRoom()
	{
		super();
		basePhyStats.setWeight(1);
		recoverPhyStats();
		climask=Places.CLIMASK_HOT;
	}
	@Override public int domainType(){return Room.DOMAIN_INDOORS_STONE;}
}
