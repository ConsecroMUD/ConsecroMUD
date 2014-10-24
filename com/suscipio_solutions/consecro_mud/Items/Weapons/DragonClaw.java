package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;



public class DragonClaw extends Natural
{
	@Override public String ID(){	return "DragonClaw";}
	public DragonClaw()
	{
		super();

		setName("a vicious dragons claw");
		setDisplayText("the claws of a dragon sit here.");
		setDescription("No doubt about it, this was the claw of a dragon.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(7);
		basePhyStats().setAttackAdjustment(2);
		basePhyStats().setDamage(8);
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
		weaponType=TYPE_SLASHING;
		weaponClassification=Weapon.CLASS_NATURAL;
	}


}
