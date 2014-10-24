package com.suscipio_solutions.consecro_mud.Abilities.Specializations;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Specialization_BluntWeapon extends Specialization_Weapon
{
	@Override public String ID() { return "Specialization_BluntWeapon"; }
	private final static String localizedName = CMLib.lang().L("Blunt Weapon Specialization");
	@Override public String name() { return localizedName; }
	public Specialization_BluntWeapon()
	{
		super();
		weaponClass=Weapon.CLASS_BLUNT;
		secondWeaponClass=Weapon.CLASS_STAFF;
	}
}
