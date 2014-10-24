package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.ListIterator;

public class ConvertingListIterator<K,L> implements ListIterator<L>
{
	private final ListIterator<K> iter;
	private final Converter<K,L> converter;
	public ConvertingListIterator(ListIterator<K> i, Converter<K,L> conv)
	{
		iter=i;
		converter=conv;
	}

	@Override
	public void add(L arg0)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public boolean hasNext()
	{
		return iter.hasNext();
	}

	@Override
	public boolean hasPrevious()
	{
		return iter.hasPrevious();
	}

	@Override
	public L next()
	{
		return converter.convert(iter.next());
	}

	@Override
	public int nextIndex()
	{
		return iter.nextIndex();
	}

	@Override
	public L previous()
	{
		return converter.convert(iter.previous());
	}

	@Override
	public int previousIndex()
	{
		return iter.previousIndex();
	}

	@Override
	public void remove()
	{
		iter.remove();
	}

	@Override
	public void set(L arg0)
	{
		throw new java.lang.IllegalArgumentException();
	}

}
