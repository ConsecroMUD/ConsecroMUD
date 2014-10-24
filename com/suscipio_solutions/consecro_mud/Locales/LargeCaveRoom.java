package com.suscipio_solutions.consecro_mud.Locales;


public class LargeCaveRoom extends CaveRoom
{
	@Override public String ID(){return "LargeCaveRoom";}
	public LargeCaveRoom()
	{
		super();
		basePhyStats.setWeight(4);
		recoverPhyStats();
	}
	@Override public int maxRange(){return 5;}
}
