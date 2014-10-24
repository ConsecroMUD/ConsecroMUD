package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class MultiListEnumeration<K> implements Enumeration<K>
{
	private final LinkedList<Iterable<K>> lists=new LinkedList<Iterable<K>>();
	private volatile Iterator<Iterable<K>> listIter = null;
	private volatile Iterator<K> iter = null;

	public MultiListEnumeration(final Iterable<K>[] esets)
	{
		if((esets!=null)&&(esets.length>0))
			lists.addAll(Arrays.asList(esets));
		setup(false);
	}

	public MultiListEnumeration(final List<List<K>> esetss, final boolean diffMethodSignature)
	{
		if((esetss!=null)&&(esetss.size()>0))
			lists.addAll(esetss);
		setup(false);
	}

	public MultiListEnumeration(final Iterable<K> eset)
	{
		lists.add(eset);
		setup(false);
	}

	private void setup(final boolean startOver)
	{
		if(startOver||(listIter==null))
			listIter=lists.iterator();
		if(startOver||(iter == null))
		{
			if(listIter.hasNext())
				iter=listIter.next().iterator();
		}
	}

	public void addEnumeration(List<K> set)
	{
		if(set != null)
			lists.add(set);
		setup(true);
	}

	@Override
	public boolean hasMoreElements()
	{
		if(iter==null) return false;
		if(iter.hasNext()) return true;
		while((!iter.hasNext())&&(listIter.hasNext()))
			iter = listIter.next().iterator();
		return iter.hasNext();
	}

	@Override
	public K nextElement()
	{
		if(!hasMoreElements())
			throw new NoSuchElementException();
		return iter.next();
	}
}
