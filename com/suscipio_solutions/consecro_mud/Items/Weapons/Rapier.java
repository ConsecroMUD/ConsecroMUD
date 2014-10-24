package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class Rapier extends Sword
{
	@Override public String ID(){	return "Rapier";}
	public Rapier()
	{
		super();

		setName("an sleek rapier");
		setDisplayText("a sleek rapier sits on the ground.");
		setDescription("It has a long, thin metal blade.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(4);
		material=RawMaterial.RESOURCE_STEEL;
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(7);
		baseGoldValue=15;
		recoverPhyStats();
		weaponType=TYPE_PIERCING;
	}


}
