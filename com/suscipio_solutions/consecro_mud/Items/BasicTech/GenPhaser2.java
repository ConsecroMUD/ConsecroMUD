package com.suscipio_solutions.consecro_mud.Items.BasicTech;


public class GenPhaser2 extends GenElecWeapon
{
	@Override public String ID(){	return "GenPhaser2";}

	public GenPhaser2()
	{
		super();
		setName("a type-II phaser");
		setDisplayText("a type-II phaser");
		super.mode = ModeType.KILL;
		super.modeTypes = new ModeType[]{ ModeType.STUN, ModeType.KILL, ModeType.DISINTEGRATE };
		setDescription("There are three activation settings: stun, kill, and disintegrate.");
	}
}
