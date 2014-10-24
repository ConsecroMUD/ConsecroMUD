package com.suscipio_solutions.consecro_mud.MOBS;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;


public class Doe extends Deer
{
	@Override public String ID(){return "Doe";}
	public Doe()
	{
		super();
		username="a doe";
		setDescription("A nervous, but beautifully graceful creation.");
		setDisplayText("A doe looks up as you happen along.");
		baseCharStats().setStat(CharStats.STAT_GENDER,'F');
	}

}
