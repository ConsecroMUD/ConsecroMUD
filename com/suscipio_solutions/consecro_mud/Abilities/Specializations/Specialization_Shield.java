package com.suscipio_solutions.consecro_mud.Abilities.Specializations;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Shield;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Specialization_Shield extends Specialization_Weapon
{
	@Override public String ID() { return "Specialization_Shield"; }
	private final static String localizedName = CMLib.lang().L("Shield Specialization");
	@Override public String name() { return localizedName; }
	public Specialization_Shield()
	{
		super();
		weaponClass=Weapon.CLASS_BLUNT;
	}

	@Override
	protected int getDamageBonus(MOB mob, int dmgType)
	{
		return getXLEVELLevel(mob);
	}
	@Override
	protected boolean isWeaponMatch(Weapon W)
	{
		return W instanceof Shield;
	}

	@Override
	protected boolean canDamage(MOB mob, Weapon W)
	{
		return W instanceof Shield;
	}

	@Override
	protected boolean isWearableItem(Item I)
	{
		return I instanceof Shield;
	}

}
