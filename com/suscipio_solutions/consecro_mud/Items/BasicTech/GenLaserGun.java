package com.suscipio_solutions.consecro_mud.Items.BasicTech;


public class GenLaserGun extends GenElecWeapon
{
	@Override public String ID(){	return "GenLaserGun";}

	public GenLaserGun()
	{
		super();
		setName("a laser pistol");
		basePhyStats.setWeight(5);
		setDisplayText("a laser pistol is sitting here");
		super.mode = ModeType.LASER;
		super.modeTypes = new ModeType[]{ ModeType.LASER };
	}
}
