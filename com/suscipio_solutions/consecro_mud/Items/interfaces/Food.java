package com.suscipio_solutions.consecro_mud.Items.interfaces;
import com.suscipio_solutions.consecro_mud.core.interfaces.Decayable;


public interface Food extends Item, Decayable
{
	public int nourishment();
	public void setNourishment(int amount);
	public int bite();
	public void setBite(int amount);
}
