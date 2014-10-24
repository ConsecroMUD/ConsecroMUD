package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class Katana extends Sword
{
	@Override public String ID(){	return "Katana";}
	public Katana()
	{
		super();

		setName("a katana");
		setDisplayText("a very ornate katana rests in the room.");
		setDescription("Just your typical, run-of-the-mill ninja sword--wrapped handle, steel blade, etc.");
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
