package com.suscipio_solutions.consecro_mud.core.collections;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ChameleonList<K> implements List<K>, SizedIterable<K>
{
	private volatile List<K> 		list;
	private volatile Signaler<K>	signaler;

	public static abstract class Signaler<K>
	{
		protected WeakReference<List<K>> 	oldReferenceListRef;

		public Signaler(final List<K> referenceList)
		{
			oldReferenceListRef = new WeakReference<List<K>>(referenceList);
		}

		public abstract void rebuild(final ChameleonList<K> me);

		public abstract boolean isDeprecated();

		public final synchronized void possiblyChangeMe(final ChameleonList<K> me)
		{
			if(!isDeprecated()) return;
			rebuild(me);
		}
	}

	public ChameleonList(final List<K> l, final Signaler<K> signaler)
	{
		list=l;
		this.signaler = signaler;
	}


	public void changeMeInto(final ChameleonList<K> fromList)
	{
		this.list=fromList.list;
		this.signaler=fromList.signaler;
	}

	public Signaler<K> getSignaler() { return signaler;}

	@Override
	public boolean add(K arg0)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public void add(int arg0, K arg1)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public boolean addAll(Collection<? extends K> arg0)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends K> arg1)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public void clear()
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public boolean contains(Object arg0)
	{
		signaler.possiblyChangeMe(this);
		return list.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0)
	{
		signaler.possiblyChangeMe(this);
		return list.containsAll(arg0);
	}

	@Override
	public K get(int arg0)
	{
		signaler.possiblyChangeMe(this);
		return list.get(arg0);
	}

	@Override
	public int indexOf(Object arg0)
	{
		signaler.possiblyChangeMe(this);
		return list.indexOf(arg0);
	}

	@Override
	public boolean isEmpty()
	{
		signaler.possiblyChangeMe(this);
		return list.isEmpty();
	}

	@Override
	public Iterator<K> iterator()
	{
		signaler.possiblyChangeMe(this);
		return new ReadOnlyIterator<K>(list.iterator());
	}

	@Override
	public int lastIndexOf(Object arg0)
	{
		signaler.possiblyChangeMe(this);
		return list.lastIndexOf(arg0);
	}

	@Override
	public ListIterator<K> listIterator()
	{
		signaler.possiblyChangeMe(this);
		return new ReadOnlyListIterator<K>(list.listIterator());
	}

	@Override
	public ListIterator<K> listIterator(int arg0)
	{
		signaler.possiblyChangeMe(this);
		return new ReadOnlyListIterator<K>(list.listIterator(arg0));
	}

	@Override
	public boolean remove(Object arg0)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public K remove(int arg0)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public boolean removeAll(Collection<?> arg0)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public boolean retainAll(Collection<?> arg0)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public K set(int arg0, K arg1)
	{
		throw new java.lang.IllegalArgumentException();
	}

	@Override
	public int size()
	{
		signaler.possiblyChangeMe(this);
		return list.size();
	}

	@Override
	public List<K> subList(int arg0, int arg1)
	{
		signaler.possiblyChangeMe(this);
		return new ReadOnlyList<K>(list.subList(arg0,arg1));
	}

	@Override
	public Object[] toArray()
	{
		signaler.possiblyChangeMe(this);
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0)
	{
		signaler.possiblyChangeMe(this);
		return list.toArray(arg0);
	}
}
