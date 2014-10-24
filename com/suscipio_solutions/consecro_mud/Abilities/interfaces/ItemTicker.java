package com.suscipio_solutions.consecro_mud.Abilities.interfaces;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;


/**
 * The interface for affects which cause an Item
 * to rejuvinate after a particular amount of time.  This interface
 * also allows the system to "source" an item back to its originating
 * room.
 * Items do not usually have tick services, so this affect ticks in
 * an items stead to allow it to rejuvinate.
 */
public interface ItemTicker extends Ability
{
	/**
	 * Registers the given item as being from the given room.  It will
	 * read the items phyStats().rejuv() value and use it as an interval
	 * for checking to see if this item is no longer in its originating
	 * room.  If so, it will create a copy of it in the originating room.
	 * @param item the item to rejuvinate
	 * @param room the room which the item is from
	 */
	public void loadMeUp(Item item, Room room);

	/**
	 * Removes the rejuvinating ticker from an item.  This
	 * is done when a room is resetting its content, and this
	 * item is no longer to be used as a source for rejuvination.
	 * @param item
	 */
	public void unloadIfNecessary(Item item);

	/**
	 * Returns the room where this item belongs
	 * @return a Room object
	 */
	public Room properLocation();

	/**
	 * Sets the room where this item belongs
	 * @param room a room object
	 */
	public void setProperLocation(Room room);

	/**
	 * Returns whether the given item is an official item
	 * being managed as a rejuving item
	 * @param item the item to check for
	 * @return true if it belongs, false otherwise
	 */
	public boolean isVerifiedContents(Item item);
}
