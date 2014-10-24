package com.suscipio_solutions.consecro_mud.Items.interfaces;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


public interface Scroll extends MiscMagic, Item, SpellHolder
{
	public boolean useTheScroll(Ability A, MOB mob);
	public boolean isReadableScrollBy(String name);
	public void setReadableScrollBy(String name);
	public void readIfAble(MOB mob, Scroll me, String spellName);
}
