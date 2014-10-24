package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class SmallDagger extends Dagger
{
	@Override public String ID(){	return "SmallDagger";}
	public SmallDagger()
	{
		super();

		basePhyStats().setDamage(3);
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
	}



}
