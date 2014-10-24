package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;


public class UniqueEntryBlockingQueue<K> extends ArrayBlockingQueue<K>
{
	private static final long serialVersionUID = 3311623439390188911L;

	public UniqueEntryBlockingQueue(int capacity)
	{
		super(capacity);
	}

	public UniqueEntryBlockingQueue(int capacity, boolean fair)
	{
		super(capacity, fair);
	}

	public UniqueEntryBlockingQueue(int capacity, boolean fair, Collection<? extends K> c)
	{
		super(capacity, fair, c);
	}

	@Override
	public synchronized boolean offer(K e)
	{
		if(!contains(e))
			return super.offer(e);
		return true;
	}

	@Override
	public synchronized boolean offer(K e, long timeout, TimeUnit unit)
			throws InterruptedException {
		if(!contains(e))
			return super.offer(e, timeout, unit);
		return true;
	}

	@Override
	public synchronized void put(K e) throws InterruptedException {
		if(!contains(e))
			super.put(e);
	}

	@Override
	public synchronized boolean add(K e)
	{
		if(!contains(e))
			return super.add(e);
		return true;
	}

	@Override
	public synchronized boolean addAll(Collection<? extends K> c)
	{
		if(c==null) return true;
		for(final K k : c)
			if(!contains(k))
				add(k);
		return true;
	}
}
