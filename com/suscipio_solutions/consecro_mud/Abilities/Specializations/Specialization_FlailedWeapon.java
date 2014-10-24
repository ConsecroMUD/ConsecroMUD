package com.suscipio_solutions.consecro_mud.Abilities.Specializations;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Specialization_FlailedWeapon extends Specialization_Weapon
{
	@Override public String ID() { return "Specialization_FlailedWeapon"; }
	private final static String localizedName = CMLib.lang().L("Flailing Weapon Specialization");
	@Override public String name() { return localizedName; }
	public Specialization_FlailedWeapon()
	{
		super();
		weaponClass=Weapon.CLASS_FLAILED;
	}
}
