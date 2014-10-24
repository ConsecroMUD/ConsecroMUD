package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;



public class Mace extends StdWeapon
{
	@Override public String ID(){	return "Mace";}
	public Mace()
	{
		super();

		setName("a rather large mace");
		setDisplayText("a heavy mace is found in the center of the room.");
		setDescription("It`s metallic and quite hard..");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(10);
		material=RawMaterial.RESOURCE_STEEL;
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(7);
		baseGoldValue=8;
		recoverPhyStats();
		weaponType=TYPE_BASHING;
		weaponClassification=Weapon.CLASS_BLUNT;
	}


}
