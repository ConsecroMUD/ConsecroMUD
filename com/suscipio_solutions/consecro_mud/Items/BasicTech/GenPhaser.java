package com.suscipio_solutions.consecro_mud.Items.BasicTech;


public class GenPhaser extends GenElecWeapon
{
	@Override public String ID(){	return "GenPhaser";}

	public GenPhaser()
	{
		super();
		setName("a phaser");
		setDisplayText("a phaser");
		setDescription("There are two activation settings: stun, and kill.");
		super.mode = ModeType.KILL;
		super.modeTypes = new ModeType[]{ ModeType.STUN, ModeType.KILL };
	}
}
