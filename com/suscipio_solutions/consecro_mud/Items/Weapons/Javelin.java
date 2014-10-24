package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;



public class Javelin extends StdWeapon
{
	@Override public String ID(){	return "Javelin";}
	public Javelin()
	{
		super();

		setName("a steel javelin");
		setDisplayText("a steel javelin sticks out from the wall.");
		setDescription("It`s metallic and quite sharp..");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(2);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(6);
		maxRange=10;
		minRange=0;
		baseGoldValue=1;
		setUsesRemaining(1);
		recoverPhyStats();
		material=RawMaterial.RESOURCE_OAK;
		weaponType=TYPE_PIERCING;
		weaponClassification=Weapon.CLASS_THROWN;
	}


}
