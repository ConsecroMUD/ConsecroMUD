package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;



public class StdBow extends StdWeapon
{
	@Override public String ID(){	return "StdBow";}
	public StdBow()
	{
		super();
		setName("a short bow");
		setDisplayText("a short bow has been left here.");
		setDescription("It looks like it might shoot arrows!");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(8);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(5);
		setAmmunitionType("arrows");
		setAmmoCapacity(20);
		setAmmoRemaining(20);
		baseGoldValue=150;
		recoverPhyStats();
		minRange=1;
		maxRange=3;
		weaponType=Weapon.TYPE_PIERCING;
		material=RawMaterial.RESOURCE_WOOD;
		weaponClassification=Weapon.CLASS_RANGED;
		setRawLogicalAnd(true);
	}
}
