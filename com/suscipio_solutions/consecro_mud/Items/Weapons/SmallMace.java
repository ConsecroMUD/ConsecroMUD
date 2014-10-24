package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;



public class SmallMace extends StdWeapon
{
	@Override public String ID(){	return "SmallMace";}
	public SmallMace()
	{
		super();

		setName("a small mace");
		setDisplayText("a small mace has been left here.");
		setDescription("It`s metallic and quite hard..");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(10);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(4);
		baseGoldValue=8;
		recoverPhyStats();
		weaponType=TYPE_BASHING;
		material=RawMaterial.RESOURCE_STEEL;
		weaponClassification=Weapon.CLASS_BLUNT;
	}


}
