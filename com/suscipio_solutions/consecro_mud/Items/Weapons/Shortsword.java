package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class Shortsword extends Sword
{
	@Override public String ID(){	return "Shortsword";}
	public Shortsword()
	{
		super();

		setName("a short sword");
		setDisplayText("a short sword has been dropped on the ground.");
		setDescription("A sword with a not-too-long blade.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(3);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(5);
		baseGoldValue=10;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
		weaponType=TYPE_PIERCING;
	}


}
