package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Shield;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;



public class ShieldWeapon extends StdWeapon implements Shield
{
	@Override public String ID(){	return "ShieldWeapon";}
	public ShieldWeapon()
	{
		super();

		setName("a bashing shield");
		setDisplayText("A bashing shield has been left here.");
		setDescription("Looks like natural fighting ability.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats().setWeight(0);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(1);
		weaponType=Weapon.TYPE_BASHING;
		material=RawMaterial.RESOURCE_STEEL;
		weaponClassification=Weapon.CLASS_BLUNT;
		recoverPhyStats();
	}

	public void setShield(Item shield)
	{
		name=shield.name();
		displayText=shield.displayText();
		miscText="";
		setDescription(shield.description());
		basePhyStats().setDamage(shield.phyStats().level());
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats().setWeight(0);
		basePhyStats().setAttackAdjustment(0);
		weaponType=Weapon.TYPE_BASHING;
		recoverPhyStats();
	}
	public ShieldWeapon(Item shield)
	{
		super();

		setShield(shield);
	}


}
