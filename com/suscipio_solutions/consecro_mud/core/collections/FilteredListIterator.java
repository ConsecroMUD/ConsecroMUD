package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class FilteredListIterator<K> implements ListIterator<K>
{
	private final ListIterator<K>  iter;
	private Filterer<K> 	filterer;
	private K 				nextElement = null;
	private K 				prevElement = null;
	private boolean 		initialized = false;

	public FilteredListIterator(ListIterator<K> eset, Filterer<K> fil)
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
		prevElement=nextElement;
		nextElement = null;
		while((nextElement==null) && (iter.hasNext()))
		{
			nextElement = iter.next();
			if(filterer.passesFilter(nextElement))
				return;
			nextElement = null;
		}
	}

	private void stagePrevElement()
	{
		nextElement=prevElement;
		prevElement = null;
		while((prevElement==null) && (iter.hasPrevious()))
		{
			prevElement = iter.previous();
			if(filterer.passesFilter(prevElement))
				return;
			prevElement = null;
		}
	}

	private void initialize()
	{
		if(!initialized)
		{
			stageNextElement();
			stagePrevElement();
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
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public void add(K e)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public boolean hasPrevious()
	{
		if(!initialized)
			initialize();
		return prevElement!=null;
	}

	@Override
	public int nextIndex()
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public K previous()
	{
		if(!hasPrevious())
			throw new NoSuchElementException();
		final K element = prevElement;
		stagePrevElement();
		return element;
	}

	@Override
	public int previousIndex()
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public void set(K e)
	{
		throw new java.lang.IllegalArgumentException();
	}
}
