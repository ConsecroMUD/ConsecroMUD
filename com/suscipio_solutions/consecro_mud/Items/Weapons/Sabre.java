package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;



public class Sabre extends StdWeapon
{
	@Override public String ID(){	return "Sabre";}
	public Sabre()
	{
		super();

		setName("a sabre");
		setDisplayText("a sabre has been dropped by someone.");
		setDescription("A slender piece of metal with a fancy silver basket-hilt.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(2);
		basePhyStats.setWeight(5);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(6);
		baseGoldValue=15;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
		weaponType=TYPE_SLASHING;//?????????
		weaponClassification=Weapon.CLASS_SWORD;
	}


}
