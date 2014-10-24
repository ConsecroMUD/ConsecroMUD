package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class Arquebus extends StdWeapon
{
	@Override public String ID(){	return "Arquebus";}
	public Arquebus()
	{
		super();

		setName("an arquebus");
		setDisplayText("an arquebus is on the ground.");
		setDescription("It\\`s got a metal barrel and wooden stock.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(15);

		basePhyStats().setAttackAdjustment(-1);
		basePhyStats().setDamage(10);

		setAmmunitionType("bullets");
		setAmmoCapacity(1);
		setAmmoRemaining(1);
		minRange=0;
		maxRange=5;
		baseGoldValue=500;
		recoverPhyStats();
		wornLogicalAnd=true;
		material=RawMaterial.RESOURCE_IRON;
		properWornBitmap=Wearable.WORN_HELD|Wearable.WORN_WIELD;
		weaponClassification=Weapon.CLASS_RANGED;
		weaponType=Weapon.TYPE_PIERCING;
	}



//	protected boolean isBackfire()
//	{
//
//	}

}
