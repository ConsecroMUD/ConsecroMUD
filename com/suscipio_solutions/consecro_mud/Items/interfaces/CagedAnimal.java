package com.suscipio_solutions.consecro_mud.Items.interfaces;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


public interface CagedAnimal extends Item
{
	public static final int ABILITY_MOBONPICKUP=0;
	public static final int ABILITY_MOBPROGRAMMATICALLY=1;

	public boolean cageMe(MOB M);
	public MOB unCageMe();
	public String cageText();
	public void setCageText(String text);
}
