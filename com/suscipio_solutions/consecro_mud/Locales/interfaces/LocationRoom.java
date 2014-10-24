package com.suscipio_solutions.consecro_mud.Locales.interfaces;



/**
 * This is a type of room that can be located as a coordinate on a
 * planet's surface.
 
 *
 */
public interface LocationRoom extends Room
{
	/**
	 * Coordinates of the place -- must be on its planet surface.
	 * Completely derived from the location of its planets code,
	 * the radius of the planet, and the direction from core.
	 * @see LocationRoom#getDirectionFromCore()
	 * @return Coordinates of the place
	 */
	public long[] coordinates();

	/**
	 * Returns the direction from the core of the planet to the
	 * location of this place on its surface.  Distance from core
	 * is always the radius of the planet.
	 * @return direction to this place from planets core.
	 */
	public double[] getDirectionFromCore();

	/**
	 * Sets the direction from the core of the planet to the
	 * location of this place on its surface.  Distance from core
	 * is always the radius of the planet.
	 * @param dir direction to this place from planets core.
	 */
	public void setDirectionFromCore(double[] dir);
}
