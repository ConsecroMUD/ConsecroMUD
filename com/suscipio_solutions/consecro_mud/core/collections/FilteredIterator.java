package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FilteredIterator<K> implements Iterator<K>
{
	private final Iterator<K>  iter;
	private Filterer<K> 	filterer;
	private K 				nextElement = null;
	private boolean 		initialized = false;

	public FilteredIterator(Iterator<K> eset, Filterer<K> fil)
	{
		iter=eset;
		filterer=fil;
	}

	public void setFilterer(Filterer<K> fil)
	{
		filterer=fil;
	}

	private void stageNextElement()
	{
		nextElement = null;
		while((nextElement==null) && (iter.hasNext()))
		{
			nextElement = iter.next();
			if(filterer.passesFilter(nextElement))
				return;
			nextElement = null;
		}
	}

	private void initialize()
	{
		if(!initialized)
		{
			stageNextElement();
			initialized=true;
		}
	}

	@Override
	public boolean hasNext()
	{
		if(!initialized)
			initialize();
		return nextElement!=null;
	}

	@Override
	public K next()
	{
		if(!hasNext())
			throw new NoSuchElementException();
		final K element = nextElement;
		stageNextElement();
		return element;
	}

	@Override
	public void remove()
	{
		throw new NoSuchElementException();
	}
}
