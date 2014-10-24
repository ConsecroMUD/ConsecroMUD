package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class StoneMaze extends StdMaze
{
	@Override public String ID(){return "StoneMaze";}
	public StoneMaze()
	{
		super();
		basePhyStats.setWeight(1);
		recoverPhyStats();
		climask=Places.CLIMASK_NORMAL;
	}
	@Override public int domainType(){return Room.DOMAIN_INDOORS_STONE;}

	@Override public String getGridChildLocaleID(){return "StoneRoom";}
}
