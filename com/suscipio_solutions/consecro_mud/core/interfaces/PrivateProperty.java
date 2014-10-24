package com.suscipio_solutions.consecro_mud.core.interfaces;


/**
 * Interface for objects which represents property purchasable by players.  May
 * be found implemented by Abilities which are placed as effects on the room  objects
 * for sale, or implemented as Items representing the sellable title.
 */
public interface PrivateProperty extends Environmental
{
	/**
	 * The value of the property in base currency values
	 * @return the price of the property
	 */
	public int getPrice();
	/**
	 * set the value of the property in base currency values
	 * @param price the price of the property
	 */
	public void setPrice(int price);
	/**
	 * Get the owner of the property, usually a clan name or a player name.
	 * @return the name of the owner of the property
	 */
	public String getOwnerName();
	/**
	 * Set the owner of the property, usually a clan name or a player name.
	 * @param owner the name of the owner of the property
	 */
	public void setOwnerName(String owner);

	/**
	 * Get the actual clan or mob owner of the property, or null if it can not.
	 * @return the owner of the property
	 */
	public CMObject getOwnerObject();

	/**
	 * Returns a unique id for this particular title and the rooms is represents, even if the contents change.
	 * @return a unique id
	 */
	public String getTitleID();
}
