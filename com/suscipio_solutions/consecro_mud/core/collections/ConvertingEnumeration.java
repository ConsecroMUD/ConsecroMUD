package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ConvertingEnumeration<K, L> implements Enumeration<L>
{
	private final Enumeration<K> enumer;
	Converter<K, L> converter;

	public ConvertingEnumeration(Enumeration<K> eset, Converter<K, L> conv)
	{
		enumer=eset;
		converter=conv;
	}

	public ConvertingEnumeration(Iterator<K> eset, Converter<K, L> conv)
	{
		enumer=new IteratorEnumeration<K>(eset);
		converter=conv;
	}

	public void setConverter(Converter<K, L> conv)
	{
		converter=conv;
	}

	@Override
	public boolean hasMoreElements()
	{
		return (converter!=null) && enumer.hasMoreElements();
	}

	@Override
	public L nextElement()
	{
		if(!hasMoreElements())
			throw new NoSuchElementException();
		return converter.convert(enumer.nextElement());
	}
}
