package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;



public class Sickle extends StdWeapon
{
	@Override public String ID(){	return "Sickle";}
	public Sickle()
	{
		super();

		setName("a sickle");
		setDisplayText("a sickle lies on the ground.");
		setDescription("A long and very curvy blade attached to a wooden handle.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(3);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(5);
		baseGoldValue=1;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_OAK;
		weaponType=TYPE_PIERCING;
		weaponClassification=Weapon.CLASS_EDGED;
	}



}
