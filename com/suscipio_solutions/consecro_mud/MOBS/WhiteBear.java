package com.suscipio_solutions.consecro_mud.MOBS;


public class WhiteBear extends BrownBear
{
	@Override public String ID(){return "WhiteBear";}
	public WhiteBear()
	{
		super();
		username="a White Bear";
		setDescription("A bear, large and husky with white fur.");
		setDisplayText("A white bear hunts here.");
	}

}
