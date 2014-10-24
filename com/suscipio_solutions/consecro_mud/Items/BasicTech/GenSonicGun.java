package com.suscipio_solutions.consecro_mud.Items.BasicTech;


public class GenSonicGun extends GenElecWeapon
{
	@Override public String ID(){	return "GenSonicGun";}

	public GenSonicGun()
	{
		super();
		setName("a sonic resonater gun");
		basePhyStats.setWeight(5);
		setDisplayText("a sonic resonater gun is sitting here");
		super.mode = ModeType.SONIC;
		super.modeTypes = new ModeType[]{ ModeType.SONIC };
	}
}
