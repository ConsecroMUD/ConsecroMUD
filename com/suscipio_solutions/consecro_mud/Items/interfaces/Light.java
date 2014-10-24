package com.suscipio_solutions.consecro_mud.Items.interfaces;


public interface Light extends Item
{
	public void setDuration(int duration);
	public int getDuration();
	public boolean destroyedWhenBurnedOut();
	public void setDestroyedWhenBurntOut(boolean truefalse);
	public boolean goesOutInTheRain();
	public boolean isLit();
	public void light(boolean isLit);

}
