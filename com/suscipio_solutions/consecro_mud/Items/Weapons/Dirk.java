package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class Dirk extends Dagger
{
	@Override public String ID(){	return "Dirk";}
	public Dirk()
	{
		super();

		setName("a dirk");
		setDisplayText("a pointy dirk is on the ground.");
		setDescription("The dirk is a single-edged, grooved weapon with a back edge near the point. ");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(1);
		baseGoldValue=2;
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(4);
		weaponType=TYPE_PIERCING;
		material=RawMaterial.RESOURCE_STEEL;
		recoverPhyStats();
	}


}
