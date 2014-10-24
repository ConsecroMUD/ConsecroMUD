package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Iterator;

public class ReadOnlyIterator<K> implements Iterator<K>
{
	private final Iterator<K> iter;
	public ReadOnlyIterator(final Iterator<K> i)
	{
		iter=i;
	}

	@Override
	public boolean hasNext()
	{
		return iter.hasNext();
	}

	@Override
	public K next()
	{
		return iter.next();
	}

	@Override
	public void remove()
	{
		throw new java.lang.IllegalArgumentException();
	}

}
