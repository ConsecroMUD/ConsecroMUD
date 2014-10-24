package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ConvertingIterator<K, L> implements Iterator<L>
{
	private final Iterator<K> iterer;
	private K currObj = null;
	Converter<K, L> converter;

	public ConvertingIterator(Iterator<K> eset, Converter<K, L> conv)
	{
		iterer=eset;
		converter=conv;
	}

	@Override
	public boolean hasNext()
	{
		return (converter!=null) && iterer.hasNext();
	}

	@Override
	public L next()
	{
		if(!hasNext())
			throw new NoSuchElementException();
		currObj = iterer.next();
		return converter.convert(currObj);
	}

	@Override
	public void remove()
	{
		iterer.remove();
	}
}
