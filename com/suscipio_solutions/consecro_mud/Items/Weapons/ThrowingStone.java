package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;




public class ThrowingStone extends StdWeapon
{
	@Override public String ID(){	return "ThrowingStone";}
	public ThrowingStone()
	{
		super();
		setName("a throwing stone");
		setDisplayText("a sharp stone has been left here.");
		setDescription("It looks like it might sail far!");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(1);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(1);
		baseGoldValue=10;
		recoverPhyStats();
		minRange=0;
		maxRange=3;
		weaponType=Weapon.TYPE_BASHING;
		material=RawMaterial.RESOURCE_STONE;
		weaponClassification=Weapon.CLASS_THROWN;
		setRawLogicalAnd(false);
	}


}
