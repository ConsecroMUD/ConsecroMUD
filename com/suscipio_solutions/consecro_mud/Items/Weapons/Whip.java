package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;



public class Whip extends StdWeapon
{
	@Override public String ID(){	return "Whip";}
	public Whip()
	{
		super();

		setName("a long leather whip");
		setDisplayText("a long leather whip has been dropped by someone.");
		setDescription("Weaved of leather with a nasty little barb at the end.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(2);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(2);
		baseGoldValue=1;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_LEATHER;
		weaponType=Weapon.TYPE_SLASHING;//?????????
		weaponClassification=Weapon.CLASS_FLAILED;
	}


}
