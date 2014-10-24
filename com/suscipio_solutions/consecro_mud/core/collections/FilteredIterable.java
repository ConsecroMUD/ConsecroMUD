package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Iterator;

public class FilteredIterable<K> implements Iterable<K>
{
	private final Iterable<K>  iter;
	private Filterer<K>  filterer;

	public FilteredIterable(Iterable<K> eset, Filterer<K> fil)
	{
		iter=eset;
		filterer=fil;
	}

	public void setFilterer(Filterer<K> fil)
	{
		filterer=fil;
	}

	@Override
	public Iterator<K> iterator() {
		return new FilteredIterator<K>(iter.iterator(),filterer);
	}
}
