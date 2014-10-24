package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationIterator<K> implements Iterator<K>
{
	private final Enumeration<K> e;

	@SuppressWarnings("unchecked")
	public EnumerationIterator(Enumeration<K> e)
	{
		if(e==null)
			this.e=EmptyEnumeration.INSTANCE;
		else
			this.e=e;
		hasNext();
	}

	@Override
	public boolean hasNext()
	{
		return e.hasMoreElements();
	}

	@Override
	public K next()
	{
		return e.nextElement();
	}

	@Override
	public void remove()
	{
		throw new java.lang.UnsupportedOperationException();
	}


}
