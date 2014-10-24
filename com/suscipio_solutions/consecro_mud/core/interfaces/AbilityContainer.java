package com.suscipio_solutions.consecro_mud.core.interfaces;

import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;


/**
*
* Something that can know or contain abilities for use.
*/
public interface AbilityContainer
{
	/**
	 * Adds a new ability to this for use.
	 * No ability with the same ID can be contained twice.
	 * @see com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability
	 * @param to the Ability to add.
	 */
	public void addAbility(Ability to);
	/**
	 * Removes the exact given ability object from here.
	 * @see com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability
	 * @param to the exact Ability to remove
	 */
	public void delAbility(Ability to);
	/**
	 * Returns the number of abilities contained herein this object.
	 * Any extraneous abilities bestowed from other sources will NOT
	 * be returned -- only the exact abilities owned herein.
	 * @see com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability
	 * @return the number of owned abilities
	 */
	public int numAbilities();
	/**
	 * Returns the Ability object at that index in this container.
	 * Any extraneous abilities bestowed from other sources MAY
	 * be returned, so long as index > numAbilities.
	 * @see com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability
	 * @param index the index of the Ability object to return
	 * @return the Ability object
	 */
	public Ability fetchAbility(int index);
	/**
	 * If contained herein, this will return the ability from this
	 * container of the given ID.
	 * Any extraneous abilities bestowed from other sources MAY
	 * be returned by this method.
	 * @see com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability
	 * @param ID the ID of the ability to return.
	 * @return the Ability object
	 */
	public Ability fetchAbility(String ID);
	/**
	 * Returns a random ability from this container.
	 * Any extraneous abilities bestowed from other sources MAY
	 * be returned by this method.
	 * @see com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability
	 * @return a random Ability
	 */
	public Ability fetchRandomAbility();
	/**
	 * Returns an enumerator of the Ability objects in this container.
	 * Any extraneous abilities bestowed from other sources will NOT
	 * be returned -- only the exact abilities owned herein.
	 * @return An enumerator for abilities
	 */
	public Enumeration<Ability> abilities();
	/**
	 * Removes all owned abilities from this container.
	 * Any extraneous abilities bestowed from other sources will NOT
	 * be removed.
	 */
	public void delAllAbilities();
	/**
	 * Returns the number of all abilities in this container.
	 * Any extraneous abilities bestowed from other sources WILL
	 * be counted by this.
	 */
	public int numAllAbilities();
	/**
	 * Returns an enumerator of the Ability objects in this container.
	 * Any extraneous abilities bestowed from other sources WILL ALSO
	 * be returned.
	 * @return An enumerator for all abilities, both in the container and not
	 */
	public Enumeration<Ability> allAbilities();

}
