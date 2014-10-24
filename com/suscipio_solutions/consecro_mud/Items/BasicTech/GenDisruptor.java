package com.suscipio_solutions.consecro_mud.Items.BasicTech;


public class GenDisruptor extends GenElecWeapon
{
	@Override public String ID(){	return "GenDisruptor";}

	protected int state=0;

	public GenDisruptor()
	{
		super();
		setName("a disruptor weapon");
		basePhyStats.setWeight(5);
		setDisplayText("a disruptor");
		setDescription("There are two activation settings: stun, and disrupt.");
		super.mode = ModeType.DISRUPT;
		super.modeTypes = new ModeType[]{ ModeType.STUN, ModeType.DISRUPT };
	}
}
