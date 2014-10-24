package com.suscipio_solutions.consecro_mud.core.collections;

public interface SizedIterable<K> extends Iterable<K>
{
	/**
	 * Returns the size of the iterable
	 * @return the size
	 */
	public int size();
}
