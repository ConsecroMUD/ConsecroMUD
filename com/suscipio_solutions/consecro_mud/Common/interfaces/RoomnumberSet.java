package com.suscipio_solutions.consecro_mud.Common.interfaces;
import java.util.Enumeration;
import java.util.Iterator;


/**
 * A class for holding CoffeeMud-style room IDs of the
 * form AreaName#[NUMBER], for instance, Midgaard#3001
 * This class is totally awesome because it stores them
 * in an efficient way (holding room ids 5-10 not as
 * 5,6,7,8,9,10, but as 5,10), but sorts them for quick
 * reads.
 *
 * Stores the internal numbers using CMIntegerGrouper
 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.CMIntegerGrouper
 */
public interface RoomnumberSet extends CMCommon
{
	/**
	 * Returns the number of room ids stored for the
	 * given Area name.
	 * @param areaName the Area to count the rooms of
	 * @return the number of room ids in the area
	 */
	public int roomCount(String areaName);

	/**
	 * Returns the total number of room ids stored here
	 * @return the total number of room ids stored here
	 */
	public int roomCountAllAreas();

	/**
	 * Returns whether any rooms at all are defined.
	 * @return true if none are defined, false otherwise
	 */
	public boolean isEmpty();

	/**
	 * Returns whether the given room id is stored here
	 * @param str the room id to look for
	 * @return true if the given room id is stored here
	 */
	public boolean contains(String str);

	/**
	 * Converts the contents of this object into an xml
	 * document.
	 * @return the contents of this object as an xml
	 */
	public String xml();

	/**
	 * Restores this object from an xml document
	 * @param xml contents for this object as an xml
	 */
	public void parseXML(String xml);

	/**
	 * Adds the given room id to this object
	 * @param str a room id
	 */
	public void add(String str);

	/**
	 * Adds a set of room ids to this object
	 * @param set the room ids to add
	 */
	public void add(RoomnumberSet set);

	/**
	 * Removes a single room id from this object
	 * @param str the room id to remove from this object
	 */
	public void remove(String str);

	/**
	 * Returns a random, fully qualified room id from those
	 * stored in here.  Includes Area name.
	 * @return a random full room id
	 */
	public String random();

	/**
	 * Returns an enumerator for all room ids in this object
	 * @return an enumerator for all room ids in this object
	 */
	public Enumeration<String> getRoomIDs();

	/**
	 * Returns an enumerator for all area names in this object
	 * @return an enumerator for all area names in this object
	 */
	public Iterator<String> getAreaNames();

	/**
	 * Returns the number parts of the room ids stored in this
	 * object for a given area.
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.CMIntegerGrouper
	 * @param areaName the area name to look for
	 * @return a set of numbers.
	 */
	public CMIntegerGrouper getGrouper(String areaName);
}
