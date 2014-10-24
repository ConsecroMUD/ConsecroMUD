package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

public class MultiIterator<K> implements Iterator<K>
{
	private final Vector<Iterator<K>> iters=new Vector<Iterator<K>>();
	private volatile int dex=0;
	private volatile Iterator<K> iter=null;

	public MultiIterator(Iterator<K>[] esets)
	{
		if((esets==null)||(esets.length==0))
			return;
		for(final Iterator<K> I : esets)
			iters.add(I);
		setup();
	}

	public MultiIterator(Iterable<K>[] esets)
	{
		if((esets==null)||(esets.length==0))
			return;
		for(final Iterable<K> I : esets)
			iters.add(I.iterator());
		setup();
	}

	public MultiIterator(Iterable<? extends Iterable<K>> esets)
	{
		if(esets==null)
			return;
		for(final Iterable<K> I : esets)
			iters.add(I.iterator());
		setup();
	}


	public MultiIterator()
	{

	}

	public void add(Iterator<K> eset)
	{
		iters.add(eset);
		setup();
	}

	private void setup()
	{
		if((iter==null)&&(dex<iters.size()))
			iter=iters.get(dex);
		while((iter!=null)&&(!iter.hasNext())&&(++dex<iters.size()))
			iter=iters.get(dex);
	}

	@Override
	public boolean hasNext()
	{
		if(iter.hasNext()) return true;
		while((!iter.hasNext())&&(++dex<iters.size()))
			iter=iters.get(dex);
		return iter.hasNext();
	}

	@Override
	public K next()
	{
		if(!hasNext())
			throw new NoSuchElementException();
		return iters.get(dex).next();
	}

	@Override
	public void remove()
	{
		if(dex>=iters.size())
			throw new NoSuchElementException();
		iters.get(dex).remove();
	}
}
