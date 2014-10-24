package com.suscipio_solutions.consecro_mud.Items.interfaces;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public interface MagicDust extends SpellHolder, MiscMagic
{
	public void spreadIfAble(MOB mob, Physical target);
}
