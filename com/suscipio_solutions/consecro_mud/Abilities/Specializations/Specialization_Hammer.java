package com.suscipio_solutions.consecro_mud.Abilities.Specializations;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Specialization_Hammer extends Specialization_Weapon
{
	@Override public String ID() { return "Specialization_Hammer"; }
	private final static String localizedName = CMLib.lang().L("Hammer Specialization");
	@Override public String name() { return localizedName; }
	public Specialization_Hammer()
	{
		super();
		weaponClass=Weapon.CLASS_HAMMER;
	}
}
