package com.suscipio_solutions.consecro_mud.Abilities.Specializations;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Specialization_Staff extends Specialization_Weapon
{
	@Override public String ID() { return "Specialization_Staff"; }
	private final static String localizedName = CMLib.lang().L("Staff Specialization");
	@Override public String name() { return localizedName; }
	public Specialization_Staff()
	{
		super();
		weaponClass=Weapon.CLASS_STAFF;
	}
}
