package com.suscipio_solutions.consecro_mud.core.interfaces;


/**
 * The interface for an item or mob which ride a Rideable
 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Rideable
 
 *
 */
public interface Rider extends PhysicalAgent
{
	/**
	 * Sets the Rideable upon which this Rider is Riding.
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Rideable
	 * @param ride the Rideable to ride upon
	 */
	public void setRiding(Rideable ride);

	/**
	 * Returns the Rideable upon which this Rider is Riding.
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Rideable
	 * @return the Rideable upon which this Rider is Riding.
	 */
	public Rideable riding();
}
