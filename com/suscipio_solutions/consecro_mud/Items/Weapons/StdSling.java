package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;



public class StdSling extends StdWeapon
{
	@Override public String ID(){	return "StdSling";}
	public StdSling()
	{
		super();
		setName("a sling");
		setDisplayText("a sling has been left here.");
		setDescription("It looks like it might shoot bullets!");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(8);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(2);
		setAmmunitionType("bullets");
		setAmmoCapacity(50);
		setAmmoRemaining(10);
		baseGoldValue=150;
		recoverPhyStats();
		minRange=1;
		maxRange=2;
		weaponType=Weapon.TYPE_BASHING;
		material=RawMaterial.RESOURCE_LEATHER;
		weaponClassification=Weapon.CLASS_RANGED;
		setRawLogicalAnd(false);
	}


}
