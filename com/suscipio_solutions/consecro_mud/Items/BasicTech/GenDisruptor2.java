package com.suscipio_solutions.consecro_mud.Items.BasicTech;


public class GenDisruptor2 extends GenElecWeapon
{
	@Override public String ID(){	return "GenDisruptor2";}

	protected int state=0;

	public GenDisruptor2()
	{
		super();
		setName("a disruptor type II weapon");
		basePhyStats.setWeight(5);
		setDisplayText("a disruptor type II ");
		setDescription("There are three activation settings: stun, maim, and disrupt.");
		super.mode = ModeType.MAIM;
		super.modeTypes = new ModeType[]{ ModeType.STUN, ModeType.MAIM, ModeType.DISRUPT };
	}
}
