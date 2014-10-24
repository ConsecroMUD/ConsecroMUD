package com.suscipio_solutions.consecro_mud.Items.interfaces;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.LocationRoom;
import com.suscipio_solutions.consecro_mud.core.interfaces.SpaceObject;


/**
 * A Space Ship, which is a space object that's dockable and can change direction.
 
 *
 */
public interface SpaceShip extends SpaceObject
{
	/**
	 * Designates that this ship is landed and docked on a planet
	 * in the given planetary room.
	 * @param R the coordinate toom in which the ship is docked.
	 */
	public void dockHere(LocationRoom R);

	/**
	 * Designates that this ship is no longer docked, and whether it
	 * should be marked as "in space".
	 * @param toSpace true to put in space, or false to leave in limbo
	 */
	public void unDock(boolean toSpace);

	/**
	 * Returns the Room where this ship is docked, or NULL if in space.
	 * @return the Room where this ship is docked, or NULL if in space.
	 */
	public LocationRoom getIsDocked();

	/**
	 * Sets and/or gets the in-air flag, letting the ship know if it is
	 * currently undergoing atmospheric shear.
	 * @param setInAirFlag null, or TRUE/FALSE to change the existing value
	 * @return the current in-air flag
	 */
	public Boolean getSetAirFlag(final Boolean setInAirFlag);

	/**
	 * Space ships are unique in having an Item stand-in for planet-side access,
	 * as well as an Area object.  This method returns the area object that 
	 * represents the contents of the ship.
	 * @return the official area version of this ship
	 */
	public Area getShipArea();

	/**
	 * Space ships are unique in having an Item stand-in for planet-side access,
	 * as well as an Area object.  This method sets the area object that 
	 * represents the contents of the ship.
	 * @param xml area xml for the ship
	 */
	public void setShipArea(String xml);

	/**
	 * Renames the ship to something else
	 * @param newName the new ship name
	 */
	public void renameSpaceShip(String newName);

	/**
	 * Space ships are unique in having an Item stand-in for planet-side access,
	 * as well as an Area object.  This method returns the object that resides in
	 * the official space grid.
	 * @return the official space version of this ship
	 */
	public SpaceObject getShipSpaceObject();

	/**
	 * The Outer Mold Line coefficient -- how streamlined are you?
	 * @return the coefficient, from 0.05-0.3
	 */
	public double getOMLCoeff();
	/**
	 * Set the Outer Mold Line coefficient -- how streamlined are you?
	 * @param coeff the Outer Mold Line coefficient
	 */
	public void setOMLCoeff(double coeff);

	/**
	 * The direction of facing of this object in radians.
	 * @return 2 dimensional array for the direction of facing
	 */
	public double[] facing();
	/**
	 * Sets the direction of facing of this object in radians.
	 * @param dir 2 dimensional array for the direction of facing
	 */
	public void setFacing(double[] dir);

	/**
	 * The orientation of the top of the object in radians.
	 * @return radian for the direction of orientation
	 */
	public double roll();
	/**
	 * Sets the orientation of the top of the object in radians.
	 * @param dir radian for the direction of orientation
	 */
	public void setRoll(double dir);
}
