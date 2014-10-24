package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;



/*
 * A version of the Vector class that provides to "safe" adds
 * and removes by copying the underlying vector whenever those
 * operations are done.
 */
public class XHashSet<T> extends HashSet<T>
{
	private static final long serialVersionUID = 6687178785122563992L;

	public XHashSet(List<T> V)
	{
		super();
		if(V!=null)
			addAll(V);
	}

	public XHashSet(T[] E)
	{
		super();
		if(E!=null)
			for(final T o : E)
				add(o);
	}

	public XHashSet(T E)
	{
		super();
		if(E!=null)
			add(E);
	}


	public XHashSet()
	{
		super();
	}

	public XHashSet(Set<T> E)
	{
		super();
		if(E!=null)
			for(final T o : E)
				add(o);
	}

	public XHashSet(Enumeration<T> E)
	{
		super();
		if(E!=null)
			for(;E.hasMoreElements();)
				add(E.nextElement());
	}

	public XHashSet(Iterator<T> E)
	{
		super();
		if(E!=null)
			for(;E.hasNext();)
				add(E.next());
	}

	public synchronized void addAll(Enumeration<T> E)
	{
		if(E!=null)
			for(;E.hasMoreElements();)
				add(E.nextElement());
	}

	public synchronized void addAll(T[] E)
	{
		if(E!=null)
			for(final T e : E)
				add(e);
	}

	public synchronized void addAll(Iterator<T> E)
	{
		if(E!=null)
			for(;E.hasNext();)
				add(E.next());
	}

	public synchronized void removeAll(Enumeration<T> E)
	{
		if(E!=null)
			for(;E.hasMoreElements();)
				remove(E.nextElement());
	}

	public synchronized void removeAll(Iterator<T> E)
	{
		if(E!=null)
			for(;E.hasNext();)
				remove(E.next());
	}

	public synchronized void removeAll(List<T> E)
	{
		if(E!=null)
			for(final T o : E)
				remove(o);
	}

	public synchronized void sort()
	{
		final Vector<T> V2=new Vector<T>(new TreeSet<T>(this));
		clear();
		addAll(V2);
	}
}
