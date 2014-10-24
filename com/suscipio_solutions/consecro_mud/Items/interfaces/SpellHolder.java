package com.suscipio_solutions.consecro_mud.Items.interfaces;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;


public interface SpellHolder extends Item
{
	public List<Ability> getSpells();
	public String getSpellList();
	public void setSpellList(String list);
}
