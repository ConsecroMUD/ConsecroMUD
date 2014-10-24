package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class Scimitar extends Sword
{
	@Override public String ID(){	return "Scimitar";}
	public Scimitar()
	{
		super();

		setName("an ornate scimitar");
		setDisplayText("a rather ornate looking Scimitar leans against the wall.");
		setDescription("It has a metallic pommel, and a long curved blade.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats().setWeight(4);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(8);
		baseGoldValue=15;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
		weaponType=TYPE_SLASHING;
	}


}
