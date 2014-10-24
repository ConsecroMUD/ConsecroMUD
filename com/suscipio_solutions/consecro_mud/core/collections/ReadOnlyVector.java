package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;


import com.suscipio_solutions.consecro_mud.core.Log;

public final class ReadOnlyVector<T> extends Vector<T>
{
	private static final long serialVersionUID = -9175373358592311411L;

	  public ReadOnlyVector()
	  {
		  super();
	  }

	  public ReadOnlyVector(List<T> E)
	  {
		  if(E!=null)
			  super.addAll(E);
	  }

	  public ReadOnlyVector(T[] E)
	  {
		  if(E!=null)
			  for(final T o : E)
				  super.add(o);
	  }

	  public ReadOnlyVector(T E)
	  {
		  if(E!=null)
			  super.add(E);
	  }

	  public ReadOnlyVector(Enumeration<T> E)
	  {
		  if(E!=null)
			  for(;E.hasMoreElements();)
				  super.add(E.nextElement());
	  }

	  public ReadOnlyVector(Iterator<T> E)
	  {
		  if(E!=null)
			  for(;E.hasNext();)
				  super.add(E.next());
	  }

	  public ReadOnlyVector(Set<T> E)
	  {
			for(final T o : E)
				super.add(o);
	  }

	  public ReadOnlyVector(int size)
	  {
		  super(size);
	  }

	@Override
	public synchronized boolean add(T t)
	{
		Log.errOut("ReadOnlyVector",new UnsupportedOperationException());
		return false;
	}
	@Override
	public synchronized void addElement(T t)
	{
		Log.errOut("ReadOnlyVector",new UnsupportedOperationException());
	}
	@Override
	public synchronized void removeElementAt(int index)
	{
		Log.errOut("ReadOnlyVector",new UnsupportedOperationException());
	}
	@Override
	public synchronized boolean removeElement(Object obj)
	{
		Log.errOut("ReadOnlyVector",new UnsupportedOperationException());
		return false;
	}
	@Override
	public boolean remove(Object o)
	{
		Log.errOut("ReadOnlyVector",new UnsupportedOperationException());
		return false;
	}
	@Override
	public void add(int index, T element)
	{
		Log.errOut("ReadOnlyVector",new UnsupportedOperationException());
	}
	@Override
	public synchronized T remove(int index)
	{
		Log.errOut("ReadOnlyVector",new UnsupportedOperationException());
		return null;
	}
	@Override
	public void clear()
	{
		Log.errOut("ReadOnlyVector",new UnsupportedOperationException());
	}
	@Override
	public synchronized boolean removeAll(Collection<?> c)
	{
		Log.errOut("ReadOnlyVector",new UnsupportedOperationException());
		return false;
	}
	@Override
	public synchronized void insertElementAt(T obj, int index)
	{
		Log.errOut("ReadOnlyVector",new UnsupportedOperationException());
	}
	@Override
	public synchronized boolean addAll(Collection<? extends T> c)
	{
		Log.errOut("ReadOnlyVector",new UnsupportedOperationException());
		return false;
	}
	@Override
	public synchronized boolean addAll(int index, Collection<? extends T> c)
	{
		Log.errOut("ReadOnlyVector",new UnsupportedOperationException());
		return false;
	}
}
