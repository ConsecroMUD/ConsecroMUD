package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Enumeration;
import java.util.Iterator;

public class IteratorEnumeration<K> implements Enumeration<K>
{
	private final Iterator<K> i;

	@SuppressWarnings("unchecked")
	public IteratorEnumeration(Iterator<K> i)
	{
		if(i==null)
			this.i=EmptyIterator.INSTANCE;
		else
			this.i=i;
		hasMoreElements();
	}

	@Override
	public boolean hasMoreElements()
	{
		return i.hasNext();
	}

	@Override
	public K nextElement()
	{
		return i.next();
	}
}
