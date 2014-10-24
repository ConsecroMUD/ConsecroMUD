package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Iterator;

public class SIterator<K> implements Iterator<K>
{
	private final Iterator<K> iter;
	private K o=null;
	public SIterator(final Iterator<K> i)
	{
		iter=i;
		nextUp();
	}
	private void nextUp()
	{
		try
		{
			if(iter.hasNext())
			{
				o=iter.next();
				return;
			}
		}
		catch(final Exception e)
		{

		}
		o=null;
	}
	@Override
	public boolean hasNext()
	{
		return o!=null;
	}
	@Override
	public K next()
	{
		final K o2=o;
		nextUp();
		return o2;
	}
	@Override public void remove() { throw new java.lang.IllegalArgumentException(); }
}
