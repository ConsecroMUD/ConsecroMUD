package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class WarHammer extends StdWeapon
{
	@Override public String ID(){	return "WarHammer";}
	public WarHammer()
	{
		super();

		setName("a warhammer");
		setDisplayText("a brutal warhammer sits here");
		setDescription("It has a large wooden handle with a brutal blunt double-head.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(10);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(6);
		baseGoldValue=25;
		wornLogicalAnd=true;
		properWornBitmap=Wearable.WORN_HELD|Wearable.WORN_WIELD;
		recoverPhyStats();
		weaponType=Weapon.TYPE_BASHING;
		material=RawMaterial.RESOURCE_STEEL;
		weaponClassification=Weapon.CLASS_HAMMER;
	}


}
