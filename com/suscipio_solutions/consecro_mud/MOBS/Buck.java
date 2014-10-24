package com.suscipio_solutions.consecro_mud.MOBS;


public class Buck extends Deer
{
	@Override public String ID(){return "Buck";}
	public Buck()
	{
		super();
		username="a buck";
		setDescription("A nervous, but beautifully graceful creation.");
		setDisplayText("A buck looks up as you happen along.");
	}

}
