package com.suscipio_solutions.consecro_mud.Exits.interfaces;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.CloseableLockable;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;
import com.suscipio_solutions.consecro_mud.core.interfaces.Readable;


/**
 * An interface for all mechanisms or pathways through which a mob may
 * travel when trying to get from one Room to another.
 * @see com.suscipio_solutions.consecro_mud.Locales.interfaces.Room
 */
public interface Exit extends PhysicalAgent, Readable, CloseableLockable
{
	/**
	 * Both reads and optionally modifies an internal reference counter for this
	 * exit.  Not currently functional.
	 * @param change 0 to make no change, or a positive or negative number
	 * @return the value of the usage counter after the change is applied
	 */
	public short exitUsage(short change);

	/**
	 * Returns the modified and qualified player-viewed description of this exit.
	 * @param mob the mob doing the viewing
	 * @param myRoom the room from which the mob sees the exit
	 * @return the description of this exit from the given player pov
	 */
	public StringBuilder viewableText(MOB mob, Room myRoom);

	/**
	 * Returns the short name of this door, e.g. gate, door, portal, etc..
	 * @see com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit#setExitParams(String, String, String, String)
	 * @return the short name of this door.
	 */
	public String doorName();

	/**
	 * Returns the verb used for describing closing this exit, e.g. close,
	 * lower, drop, etc..
	 * @see com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit#openWord()
	 * @see com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit#hasADoor()
	 * @see com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit#defaultsClosed()
	 * @see com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit#setExitParams(String, String, String, String)
	 * @return the short name of a verb used when closing this door.
	 */
	public String closeWord();

	/**
	 * Returns the verb used for describing opening this exit, e.g. open,
	 * raise, lift, etc..
	 * @see com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit#closeWord()
	 * @see com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit#hasADoor()
	 * @see com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit#defaultsClosed()
	 * @see com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit#setExitParams(String, String, String, String)
	 * @return the short name of a verb used when opening this door.
	 */
	public String openWord();

	/**
	 * Returns a text description of what this exit looks like when closed.
	 * @see com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit#hasADoor()
	 * @see com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit#defaultsClosed()
	 * @return A description of this exit when closed.
	 */
	public String closedText();

	/**
	 * Sets various word descriptions used when doing some dynamic manipulations
	 * of this exit.
	 * @see com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit#closeWord()
	 * @see com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit#openWord()
	 * @see com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit#doorName()
	 * @see com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit#closedText()
	 * @param newDoorName short name of this door, e.g. door, gate
	 * @param newCloseWord verb used to close this door, e.g. close, lower
	 * @param newOpenWord verb used to open this door, e.g. open, raise
	 * @param newClosedText description of this exit when in a closed state
	 */
	public void setExitParams(String newDoorName,
							  String newCloseWord,
							  String newOpenWord,
							  String newClosedText);


	/**
	 * If this exit represents a route to a room not yet created, but whose room id
	 * is already known, this will return that designated room id.
	 * @see com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit#setTemporaryDoorLink(String)
	 * @return the room id of the room that will be linked in later
	 */
	public String temporaryDoorLink();

	/**
	 * If this exit represents a route to a room not yet created, but whose room id
	 * is already known, this will set that designated room id.
	 * @see com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit#temporaryDoorLink()
	 * @param link the room id of the room that will be linked in later
	 */
	public void setTemporaryDoorLink(String link);

	/**
	 * Returns the very last room from or to which this exit was used.
	 * @return the last room that mattered to this exit
	 */
	public Room lastRoomUsedFrom();
}
