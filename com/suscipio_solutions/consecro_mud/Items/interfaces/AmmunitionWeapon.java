package com.suscipio_solutions.consecro_mud.Items.interfaces;


public interface AmmunitionWeapon extends Weapon
{
	public boolean requiresAmmunition();
	public void setAmmunitionType(String ammo);
	public String ammunitionType();
	public int ammunitionRemaining();
	public void setAmmoRemaining(int amount);
	public int ammunitionCapacity();
	public void setAmmoCapacity(int amount);
}
