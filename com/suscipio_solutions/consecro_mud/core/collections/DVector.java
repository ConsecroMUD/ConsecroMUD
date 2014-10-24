package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;


@SuppressWarnings({"unchecked","rawtypes"})
public class DVector implements Cloneable, java.io.Serializable
{
	public static final long 	serialVersionUID=43353454350L;
	protected int 				dimensions=1;
	private SVector<Object[]> 	stuff;
	private final static int 	MAX_SIZE=9;

	public DVector(int dim)
	{
		if(dim<1) throw new java.lang.IndexOutOfBoundsException();
		if(dim>MAX_SIZE) throw new java.lang.IndexOutOfBoundsException();
		dimensions=dim;
		stuff=new SVector<Object[]>(1);
	}
	public DVector(int dim, int startingSize)
	{
		if(dim<1) throw new java.lang.IndexOutOfBoundsException();
		if(dim>MAX_SIZE) throw new java.lang.IndexOutOfBoundsException();
		dimensions=dim;
		stuff=new SVector<Object[]>(startingSize);
	}

	public synchronized void clear()
	{
		stuff.clear();
	}

	public synchronized void trimToSize()
	{
		stuff.trimToSize();
	}

	public synchronized int indexOf(Object O)
	{
		int x=0;
		if(O==null)
		{
			for(final Enumeration<Object[]> e=stuff.elements();e.hasMoreElements();x++)
				if(e.nextElement()[0]==null)
					return x;
		}
		else
		for(final Enumeration<Object[]> e=stuff.elements();e.hasMoreElements();x++)
			if(O.equals(e.nextElement()[0]))
				return x;
		return -1;
	}
	public synchronized Object[] elementsAt(int x)
	{
		if((x<0)||(x>=stuff.size())) throw new java.lang.IndexOutOfBoundsException();
		return stuff.elementAt(x);
	}

	public synchronized Object[] removeElementsAt(int x)
	{
		if((x<0)||(x>=stuff.size())) throw new java.lang.IndexOutOfBoundsException();
		final Object[] O=stuff.elementAt(x);
		stuff.removeElementAt(x);
		return O;
	}

	public synchronized DVector copyOf()
	{
		final DVector V=new DVector(dimensions);
		if(stuff!=null)
		{
			for (final Object[] name : stuff)
				V.stuff.addElement(name.clone());
		}
		return V;
	}

	public synchronized void sortBy(int dim)
	{
		if((dim<1)||(dim>dimensions)) throw new java.lang.IndexOutOfBoundsException();
		dim--;
		if(stuff!=null)
		{
			final TreeSet sorted=new TreeSet();
			Object O=null;
			for (final Object[] name : stuff)
			{
				O=(name)[dim];
				if(!sorted.contains(O))
					sorted.add(O);
			}
			final SVector<Object[]> newStuff = new SVector<Object[]>(stuff.size());
			for(final Iterator i=sorted.iterator();i.hasNext();)
			{
				O=i.next();
				for (final Object[] Os : stuff)
				{
					if(O==Os[dim]) newStuff.addElement(Os);
				}
			}
			stuff=newStuff;
		}
	}

	public static DVector toDVector(Hashtable h)
	{
		final DVector DV=new DVector(2);
		for(final Enumeration e=h.keys();e.hasMoreElements();)
		{
			final Object key=e.nextElement();
			DV.addElement(key,h.get(key));
		}
		return DV;
	}

	public synchronized void addSharedElements(Object[] O)
	{
		if(dimensions!=O.length) throw new java.lang.IndexOutOfBoundsException();
		stuff.addElement(O);
	}
	public synchronized void addElement(Object... Os)
	{
		if(dimensions!=Os.length) throw new java.lang.IndexOutOfBoundsException();
		stuff.addElement(Os);
	}
	public boolean contains(Object O)
	{
		return indexOf(O)>=0;
	}
	public synchronized boolean containsIgnoreCase(String S)
	{
		if(S==null) return indexOf(null)>=0;
		for (final Object[] name : stuff)
			if(S.equalsIgnoreCase(name[0].toString()))
				return true;
		return false;
	}
	public int size()
	{
		return stuff.size();
	}
	public synchronized void removeElementAt(int i)
	{
		if(i>=0)
			stuff.removeElementAt(i);
	}
	public synchronized void removeElement(Object O)
	{
		removeElementAt(indexOf(O));
	}
	public synchronized Vector getDimensionVector(int dim)
	{
		final Vector V=new Vector<Object>(stuff.size());
		if(dimensions<dim) throw new java.lang.IndexOutOfBoundsException();
		for (final Object[] name : stuff)
			V.addElement(name[dim-1]);
		return V;
	}
	public synchronized Vector getRowVector(int row)
	{
		final Vector V=new Vector<Object>(dimensions);
		final Object[] O=elementsAt(row);
		for (final Object element : O)
			V.add(element);
		return V;
	}
	public synchronized Object elementAt(int i, int dim)
	{
		if(dimensions<dim) throw new java.lang.IndexOutOfBoundsException();
		return (stuff.elementAt(i))[dim-1];
	}

	public synchronized void setElementAt(int index, int dim, Object O)
	{
		if(dimensions<dim) throw new java.lang.IndexOutOfBoundsException();
		stuff.elementAt(index)[dim-1]=O;
	}

	public synchronized void insertElementAt(int here, Object... Os)
	{
		if(dimensions!=Os.length) throw new java.lang.IndexOutOfBoundsException();
		stuff.insertElementAt(Os,here);
	}
}
