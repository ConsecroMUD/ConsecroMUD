package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ReadOnlyList<K> implements List<K>
{
	private final List<K> list;
	public ReadOnlyList(List<K> l)
	{
		list=l;
	}
	@Override
	public boolean add(K arg0)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public void add(int arg0, K arg1)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public boolean addAll(Collection<? extends K> arg0)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends K> arg1)
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
		return list.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0)
	{
		return list.containsAll(arg0);
	}

	@Override
	public K get(int arg0)
	{
		return list.get(arg0);
	}

	@Override
	public int indexOf(Object arg0)
	{
		return list.indexOf(arg0);
	}

	@Override
	public boolean isEmpty()
	{
		return list.isEmpty();
	}

	@Override
	public Iterator<K> iterator()
	{
		return new ReadOnlyIterator<K>(list.iterator());
	}

	@Override
	public int lastIndexOf(Object arg0)
	{
		return list.lastIndexOf(arg0);
	}

	@Override
	public ListIterator<K> listIterator()
	{
		return new ReadOnlyListIterator<K>(list.listIterator());
	}

	@Override
	public ListIterator<K> listIterator(int arg0)
	{
		return new ReadOnlyListIterator<K>(list.listIterator(arg0));
	}

	@Override
	public boolean remove(Object arg0)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public K remove(int arg0)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public boolean removeAll(Collection<?> arg0)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public boolean retainAll(Collection<?> arg0)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public K set(int arg0, K arg1)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public int size()
	{
		return list.size();
	}

	@Override
	public List<K> subList(int arg0, int arg1)
	{
		return new ReadOnlyList<K>(list.subList(arg0,arg1));
	}

	@Override
	public Object[] toArray()
	{
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0)
	{
		return list.toArray(arg0);
	}

}
