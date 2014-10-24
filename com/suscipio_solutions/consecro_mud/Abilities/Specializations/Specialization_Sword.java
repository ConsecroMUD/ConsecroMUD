package com.suscipio_solutions.consecro_mud.Abilities.Specializations;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Specialization_Sword extends Specialization_Weapon
{
	@Override public String ID() { return "Specialization_Sword"; }
	private final static String localizedName = CMLib.lang().L("Sword Specialization");
	@Override public String name() { return localizedName; }
	public Specialization_Sword()
	{
		super();
		weaponClass=Weapon.CLASS_SWORD;
	}
}
