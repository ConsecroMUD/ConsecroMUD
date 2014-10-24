package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ReadOnlySet<K> implements Set<K>
{
	private final Set<K> set;
	public ReadOnlySet()
	{
		set=new HashSet<K>();
	}
	public ReadOnlySet(Set<K> s)
	{
		set=s;
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
	public boolean contains(Object arg0)
	{
		return set.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0)
	{
		return set.containsAll(arg0);
	}

	@Override
	public boolean isEmpty()
	{
		return set.isEmpty();
	}

	@Override
	public Iterator<K> iterator()
	{
		return new ReadOnlyIterator<K>(set.iterator());
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
		return set.size();
	}

	@Override
	public Object[] toArray()
	{
		return set.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0)
	{
		return set.toArray(arg0);
	}
}
