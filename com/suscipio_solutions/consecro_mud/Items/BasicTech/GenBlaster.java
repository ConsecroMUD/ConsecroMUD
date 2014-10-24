package com.suscipio_solutions.consecro_mud.Items.BasicTech;


public class GenBlaster extends GenElecWeapon
{
	@Override public String ID(){	return "GenBlaster";}

	public GenBlaster()
	{
		super();
		setName("a blaster gun");
		basePhyStats.setWeight(5);
		setDisplayText("a blaster gun is sitting here");
		super.mode = ModeType.KILL;
		super.modeTypes = new ModeType[]{ ModeType.KILL };
	}
}
