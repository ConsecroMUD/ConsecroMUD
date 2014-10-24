package com.suscipio_solutions.consecro_mud.Items.interfaces;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


public interface Pill extends Food, MiscMagic, SpellHolder
{
	public void eatIfAble(MOB mob);
}
