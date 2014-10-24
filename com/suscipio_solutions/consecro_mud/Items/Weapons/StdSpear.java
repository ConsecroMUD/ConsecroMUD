package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;




public class StdSpear extends StdWeapon
{
	@Override public String ID(){	return "StdSpear";}
	public StdSpear()
	{
		super();
		setName("a spear");
		setDisplayText("a spear has been left here.");
		setDescription("It looks like it might sail far!");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(8);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(5);
		baseGoldValue=10;
		recoverPhyStats();
		minRange=0;
		maxRange=3;
		weaponType=Weapon.TYPE_PIERCING;
		material=RawMaterial.RESOURCE_WOOD;
		weaponClassification=Weapon.CLASS_THROWN;
		setRawLogicalAnd(false);
	}


}
