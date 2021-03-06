package com.suscipio_solutions.consecro_mud.core.interfaces;

/**
 * The general base interface which is implemented by every class
 * which the CoffeeMud ClassLoader (CMClass) handles.
 * @see com.suscipio_solutions.consecro_mud.core.CMClass
 */
public interface CMObject extends Cloneable, Comparable<CMObject>
{
	/**
	 * The CoffeeMud Java Class ID shared by all instances of
	 * this object.  Unlike the Java Class name, this method
	 * does not include package information.  However, it must
	 * return a String value unique to its class category in
	 * the ClassLoader.  Class categories include Libraries, Common,
	 * Areas, Abilities, Behaviors, CharClasses, Commands, Exits
	 * Locales, MOBS, Races, WebMacros, Basic Items, Armor,
	 * Weapons, ClanItems, Tech.  The name is typically identical
	 * to the class name.
	 * @return the name of this class
	 */
	public String ID();
	/**
	 * The displayable name of this object.  May be modified by phyStats() object. Is
	 * derived from the Name().
	 * @see  Environmental#Name()
	 * @return the modified final name of this object on the map.
	 */
	public String name();
	/**
	 * Returns a new instance of this class.
	 * @return a new instance of this class
	 */
	public CMObject newInstance();
	/**
	 * Similar to Cloneable.clone(), but does its best to make sure that
	 * any internal objects to this class are also copyOfed.
	 * @return a clone of this object
	 */
	public CMObject copyOf();

	/**
	 * Called ONCE after all objects are loaded, but before the map is read in
	 * during initialization.
	 */
	public void initializeClass();
	
}
