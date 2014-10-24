package com.suscipio_solutions.consecro_mud.core.collections;

import java.util.List;



/**
 * A searchable list, usually sorted or otherwise made worth searching.
 * @param <T>
 */
public interface SearchIDList<T> extends List<T>
{
	/**
	 * Searches the sorted list of objects for one with the
	 * given ID;
	 * @param arg0 the ID of the Object to look for
	 * @return the object or null if not found
	 */
	public T find(String arg0);

	/**
	 * Searches the sorted list of objects for one with the
	 * same ID as the object given.
	 * @param arg0 the Object like the one to look for
	 * @return the object or null if not found
	 */
	public T find(T arg0);
}
