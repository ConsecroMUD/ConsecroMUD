package com.suscipio_solutions.consecro_mud.core.interfaces;


/**
 * A Drinkable object containing its own liquid material type, and liquid capacity management.
 */
public interface Modifiable
{
	/**
	 * Returns an array of the string names of those fields which are modifiable on this object at run-time by
	 * builders.
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Modifiable#getStat(String)
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Modifiable#setStat(String, String)
	 * @return list of the fields which may be set.
	 */
	public String[] getStatCodes();

	/**
	 * Returns the index into the stat codes array where extra savable fields begins.
	 * This number is always the same as getStatCodes().length unless there are extra
	 * fields which need to be saved in xml for generic objects.  This method is used
	 * by editors for post-build user-defined fields.
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Modifiable#getStatCodes()
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Modifiable#getStat(String)
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Modifiable#setStat(String, String)
	 * @return the index into getStatCodes()
	 */
	public int getSaveStatIndex();

	/**
	 * An alternative means of retreiving the values of those fields on this object which are modifiable at
	 * run-time by builders.  See getStatCodes() for possible values for the code passed to this method.
	 * Values returned are always strings, even if the field itself is numeric or a list.
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Modifiable#getStatCodes()
	 * @param code the name of the field to read.
	 * @return the value of the field read
	 */
	public String getStat(String code);
	/**
	 * An alternative means of retreiving the values of those fields on this object which are modifiable at
	 * run-time by builders.  See getStatCodes() for possible values for the code passed to this method.
	 * Values returned are always strings, even if the field itself is numeric or a list.
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Modifiable#getStatCodes()
	 * @param code the name of the field to read.
	 * @return true if the code is a real value, false otherwise
	 */
	public boolean isStat(String code);
	/**
	 * An alternative means of setting the values of those fields on this object which are modifiable at
	 * run-time by builders.  See getStatCodes() for possible values for the code passed to this method.
	 * The value passed in is always a string, even if the field itself is numeric or a list.
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Modifiable#getStatCodes()
	 * @param code the name of the field to set
	 * @param val the value to set the field to
	 */
	public void setStat(String code, String val);
}
