package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;



public class Cestus extends StdWeapon
{
	@Override public String ID(){	return "Cestus";}
	public Cestus()
	{
		super();

		setName("a mean looking cestus");
		setDisplayText("a cestus is on the gound.");
		setDescription("It\\`s a glove covered in long spikes and blades.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(2);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(4);
		material=RawMaterial.RESOURCE_LEATHER;
		baseGoldValue=5;
		recoverPhyStats();
		weaponType=Weapon.TYPE_PIERCING;
		weaponClassification=Weapon.CLASS_EDGED;

	}



}
