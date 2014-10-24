package com.suscipio_solutions.consecro_mud.Abilities.Specializations;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Specialization_Polearm extends Specialization_Weapon
{
	@Override public String ID() { return "Specialization_Polearm"; }
	private final static String localizedName = CMLib.lang().L("Polearm Specialization");
	@Override public String name() { return localizedName; }
	public Specialization_Polearm()
	{
		super();
		weaponClass=Weapon.CLASS_POLEARM;
	}
}
