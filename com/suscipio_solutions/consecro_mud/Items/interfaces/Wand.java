package com.suscipio_solutions.consecro_mud.Items.interfaces;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public interface Wand extends MiscMagic
{
	public void setSpell(Ability theSpell);
	public Ability getSpell();

	public boolean checkWave(MOB mob, String message);
	public void waveIfAble(MOB mob, Physical afftarget, String message);
	public String magicWord();
	public int maxUses();
	public void setMaxUses(int maxUses);
}
