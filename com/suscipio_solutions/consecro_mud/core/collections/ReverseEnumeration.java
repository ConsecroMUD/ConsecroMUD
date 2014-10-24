package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

public class ReverseEnumeration<K> implements Enumeration<K>
{
	private int index;
	private final List<K> set;
	public ReverseEnumeration(List<K> eset)
	{
		set=eset;
		index=set.size();
		hasMoreElements();
	}

	@Override
	public boolean hasMoreElements()
	{
		while(index>set.size()) index--;
		return (index>0);
	}

	@Override
	public K nextElement()
	{
		if(!hasMoreElements())
			throw new NoSuchElementException();
		index--;
		return set.get(index);
	}
}
