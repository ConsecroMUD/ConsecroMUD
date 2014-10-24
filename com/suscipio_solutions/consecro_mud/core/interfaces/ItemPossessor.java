package com.suscipio_solutions.consecro_mud.core.interfaces;

import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;


/**
 * An interface for something capable of possessing Items
 */
public interface ItemPossessor extends PhysicalAgent, ItemCollection
{

	/**
	 * Adds a new item to its possessor, with an expiration code.
	 * Duplicates will not be permitted.
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor.Expire
	 * @param item the item to add
	 */
	public void addItem(Item item, Expire expire);

	/**
	 * Intelligently removes an item from its current location and
	 * moves it to this possessor, managing any container contents,
	 * and possibly followers/riders if the item is a cart.  An
	 * expiration can be set on the move to have the items expire.
	 * Duplicates will not be permitted.
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor.Expire
	 * @param container the item to add
	 * @param expire the expiration code
	 * @param moveFlags any flags related to the move
	 */
	public void moveItemTo(Item container, Expire expire, Move... moveFlags);

	/**
	 * Intelligently removes an item from its current location and
	 * moves it to this possessor, managing any container contents.
	 * Is the same as calling the longer moveItemTo with a Never
	 * expiration, and NO movement flags.
	 * Duplicates will not be permitted.
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor.Expire
	 * @param container the item to add
	 */
	public void moveItemTo(Item container);

	/** constants for the addItem methods to denote how long the item lives before expiring */
	public enum Expire { Never, Monster_EQ, Player_Drop, Resource, Monster_Body, Player_Body	}

	/** constant for the moveItemTo methods to denote flags are being given -- normal operation */
	public enum Move { Followers}

	/** constant for the findItem/findItems method denoting special modifying flags on the search */
	public enum Find { WornOnly, UnwornOnly, AddCoins, RespectLocation}
}
