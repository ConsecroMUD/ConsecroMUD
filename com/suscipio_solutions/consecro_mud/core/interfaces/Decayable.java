package com.suscipio_solutions.consecro_mud.core.interfaces;


/**
 * A Drinkable object containing its own liquid material type, and liquid capacity management.
 */
public interface Decayable extends Environmental
{
	/**
	 * The time, in milliseconds, when this will rot.  0=never
	 * @see Decayable#setDecayTime(long)
	 * @return the time in milliseconds when this will rot. 0=never
	 */
	public long decayTime();
	/**
	 * Sets the time, in milliseconds, when this will rot.  0=never
	 * @param time in milliseconds, when this will rot.  0=never
	 * @see Decayable#decayTime()
	 */
	public void setDecayTime(long time);
}
