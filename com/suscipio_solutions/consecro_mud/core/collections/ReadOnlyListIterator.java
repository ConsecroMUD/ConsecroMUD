package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.ListIterator;

public class ReadOnlyListIterator<K> implements ListIterator<K>
{
	private final ListIterator<K> iter;
	public ReadOnlyListIterator(ListIterator<K> i)
	{
		iter=i;
	}

	@Override
	public void add(K arg0)
	{
		iter.add(arg0);
	}

	@Override
	public boolean hasNext()
	{
		return iter.hasNext();
	}

	@Override
	public boolean hasPrevious()
	{
		return iter.hasPrevious();
	}

	@Override
	public K next()
	{
		return iter.next();
	}

	@Override
	public int nextIndex()
	{
		return iter.nextIndex();
	}

	@Override
	public K previous()
	{
		return iter.previous();
	}

	@Override
	public int previousIndex()
	{
		return iter.previousIndex();
	}

	@Override
	public void remove()
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public void set(K arg0)
	{
		throw new java.lang.IllegalArgumentException();
	}

}
