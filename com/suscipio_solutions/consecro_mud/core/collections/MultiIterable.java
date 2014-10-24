package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Arrays;
import java.util.Iterator;

public class MultiIterable<K> implements Iterable<K>, SizedIterable<K>
{
	@SuppressWarnings("unchecked")
	private Iterable<K>[] iters=new Iterable[0];
	private int size=0;

	public MultiIterable(Iterable<K>[] esets, int newSize)
	{
		if((esets==null)||(esets.length==0))
			return;
		iters=esets.clone();
		size=newSize;
	}

	public MultiIterable()
	{
	}

	public synchronized void add(Iterable<K> eset, int sizeAdd)
	{
		iters=Arrays.copyOf(iters, iters.length+1);
		iters[iters.length-1]=eset;
		size+=sizeAdd;
	}

	@Override
	public Iterator<K> iterator()
	{
		return new MultiIterator<K>(iters);
	}

	@Override
	public int size()
	{
		return size;
	}

}
