package com.suscipio_solutions.consecro_mud.Locales;


public class LargeWoodRoom extends WoodRoom
{
	@Override public String ID(){return "LargeWoodRoom";}
	public LargeWoodRoom()
	{
		super();
		basePhyStats.setWeight(3);
		recoverPhyStats();
	}
	@Override public int maxRange(){return 5;}
}
