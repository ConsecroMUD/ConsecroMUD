package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;



public class Dagger extends StdWeapon
{
	@Override public String ID(){	return "Dagger";}
	public Dagger()
	{
		super();

		setName("a small dagger");
		setDisplayText("a sharp little dagger lies here.");
		setDescription("It has a wooden handle and a metal blade.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(1);
		baseGoldValue=2;
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(4);
		weaponType=TYPE_PIERCING;
		material=RawMaterial.RESOURCE_STEEL;
		weaponClassification=Weapon.CLASS_DAGGER;
		recoverPhyStats();
	}



}
