package com.suscipio_solutions.consecro_mud.core.collections;

import java.util.Comparator;
import java.util.List;



public class CustSortListWrap<T extends Comparable<T>> extends SortedListWrap<T>
{
	private final Comparator<T> comparator;
	public CustSortListWrap(List<T> list, Comparator<T> comparator)
	{
		super(list);
		this.comparator=comparator;
	}

	@SuppressWarnings("unchecked")
	@Override protected int compareTo(T arg0, Object arg1)
	{

		if(arg0 == null)
		{
			if(arg1 == null)
				return 0;
			return -1;
		}
		else
		if(arg1 == null)
			return 1;
		else
			return comparator.compare(arg0, (T)arg1);
	}
}
