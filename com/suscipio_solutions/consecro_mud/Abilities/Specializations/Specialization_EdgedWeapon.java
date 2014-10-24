package com.suscipio_solutions.consecro_mud.Abilities.Specializations;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Specialization_EdgedWeapon extends Specialization_Weapon
{
	@Override public String ID() { return "Specialization_EdgedWeapon"; }
	private final static String localizedName = CMLib.lang().L("Edged Weapon Specialization");
	@Override public String name() { return localizedName; }
	public Specialization_EdgedWeapon()
	{
		super();
		weaponClass=Weapon.CLASS_EDGED;
		secondWeaponClass=Weapon.CLASS_DAGGER;
	}
}
