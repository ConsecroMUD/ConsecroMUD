package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class Longsword extends Sword
{
	@Override public String ID(){	return "Longsword";}
	public Longsword()
	{
		super();

		setName("a fancy longsword");
		setDisplayText("a fancy longsword has been dropped on the ground.");
		setDescription("A standard one-handed sword.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(4);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(8);
		baseGoldValue=15;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
		weaponType=TYPE_SLASHING;
	}


}
