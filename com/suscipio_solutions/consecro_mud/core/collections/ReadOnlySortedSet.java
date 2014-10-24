package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

public class ReadOnlySortedSet<K> implements SortedSet<K>
{
	private final SortedSet<K> set;
	public ReadOnlySortedSet(SortedSet<K> s)
	{
		set=s;
	}
	@Override
	public Comparator<? super K> comparator()
	{
		return set.comparator();
	}
	@Override
	public K first()
	{
		return set.first();
	}
	@Override
	public SortedSet<K> headSet(K arg0)
	{
		return new ReadOnlySortedSet<K>(set.headSet(arg0));
	}
	@Override
	public K last()
	{
		return set.last();
	}
	@Override
	public SortedSet<K> subSet(K arg0, K arg1)
	{
		return new ReadOnlySortedSet<K>(set.subSet(arg0,arg1));
	}
	@Override
	public SortedSet<K> tailSet(K arg0)
	{
		return new ReadOnlySortedSet<K>(set.tailSet(arg0));
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
