package com.suscipio_solutions.consecro_mud.Locales;


public class LargeStoneRoom extends StoneRoom
{
	@Override public String ID(){return "LargeStoneRoom";}
	public LargeStoneRoom()
	{
		super();
		basePhyStats.setWeight(3);
		recoverPhyStats();
	}
	@Override public int maxRange(){return 5;}
}
