package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class BattleAxe extends Sword
{
	@Override public String ID(){	return "BattleAxe";}
	public BattleAxe()
	{
		super();

		setName("a battle axe");
		setDisplayText("a heavy battle axe sits here");
		setDescription("It has a stout pole, about 4 feet in length with a trumpet shaped blade.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(15);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(8);
		baseGoldValue=35;
		wornLogicalAnd=true;
		material=RawMaterial.RESOURCE_STEEL;
		properWornBitmap=Wearable.WORN_HELD|Wearable.WORN_WIELD;
		recoverPhyStats();
		weaponType=Weapon.TYPE_SLASHING;
		weaponClassification=Weapon.CLASS_AXE;
	}


}
