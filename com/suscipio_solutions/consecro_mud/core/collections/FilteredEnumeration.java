package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class FilteredEnumeration<K> implements Enumeration<K>
{
	private final Enumeration<K>  enumer;
	private Filterer<K> 	filterer;
	private K 				nextElement = null;
	private boolean 		initialized = false;

	public FilteredEnumeration(Enumeration<K> eset, Filterer<K> fil)
	{
		enumer=eset;
		filterer=fil;
	}

	public void setFilterer(Filterer<K> fil)
	{
		filterer=fil;
	}

	private void stageNextElement()
	{
		nextElement = null;
		while((nextElement==null) && (enumer.hasMoreElements()))
		{
			nextElement = enumer.nextElement();
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
	public boolean hasMoreElements()
	{
		if(!initialized)
			initialize();
		return nextElement!=null;
	}

	@Override
	public K nextElement()
	{
		if(!hasMoreElements())
			throw new NoSuchElementException();
		final K element = nextElement;
		stageNextElement();
		return element;
	}
}
