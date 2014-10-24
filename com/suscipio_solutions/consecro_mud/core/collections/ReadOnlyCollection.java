package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Collection;
import java.util.Iterator;

public class ReadOnlyCollection<K> implements Collection<K>
{
	private final Collection<K> col;
	public ReadOnlyCollection(final Collection<K> c)
	{
		col=c;
	}
	@Override
	public boolean add(K e)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public boolean addAll(Collection<? extends K> c)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public void clear()
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public boolean contains(Object o)
	{
		return col.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return col.containsAll(c);
	}

	@Override
	public boolean isEmpty()
	{
		return col.isEmpty();
	}

	@Override
	public Iterator<K> iterator()
	{
		return new ReadOnlyIterator<K>(col.iterator());
	}

	@Override
	public boolean remove(Object o)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public int size()
	{
		return col.size();
	}

	@Override
	public Object[] toArray()
	{
		return col.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return col.toArray(a);
	}
}
