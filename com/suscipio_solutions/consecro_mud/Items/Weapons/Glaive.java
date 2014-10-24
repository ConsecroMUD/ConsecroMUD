package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class Glaive extends StdWeapon
{
	@Override public String ID(){	return "Glaive";}
	public Glaive()
	{
		super();

		setName("a heavy glaive");
		setDisplayText("a glaive leans against the wall.");
		setDescription("A long blade on a pole.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(8);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(6);
		weaponType=TYPE_SLASHING;
		baseGoldValue=6;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
		wornLogicalAnd=true;
		properWornBitmap=Wearable.WORN_HELD|Wearable.WORN_WIELD;
		weaponClassification=Weapon.CLASS_POLEARM;
	}




}
