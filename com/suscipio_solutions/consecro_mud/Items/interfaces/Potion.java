package com.suscipio_solutions.consecro_mud.Items.interfaces;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public interface Potion extends Drink, MiscMagic, SpellHolder
{
	public boolean isDrunk();
	public void setDrunk(boolean isTrue);
	public void drinkIfAble(MOB owner, Physical drinkerTarget);
}
