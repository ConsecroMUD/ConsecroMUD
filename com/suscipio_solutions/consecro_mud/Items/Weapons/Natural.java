package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.core.CMLib;





public class Natural extends StdWeapon
{
	@Override public String ID(){	return "Natural";}
	public Natural()
	{
		super();

		setName("fingernails and teeth");
		setDisplayText("A set of claws and teeth are piled here.");
		setDescription("Those hands and claws look fit to kill.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(0);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(0);
		weaponType=TYPE_NATURAL;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_SCALES;
		weaponClassification=Weapon.CLASS_NATURAL;
	}


	@Override
	public String hitString(int damageAmount)
	{
		return "<S-NAME> "+CMLib.combat().standardHitWord(weaponType,damageAmount)+" <T-NAMESELF>";
	}
}
