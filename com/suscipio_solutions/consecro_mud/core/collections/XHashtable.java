package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



/*
 * A version of the Vector class that provides to "safe" adds
 * and removes by copying the underlying vector whenever those
 * operations are done.
 */
public class XHashtable<K,V> extends Hashtable<K,V>
{
	private static final long serialVersionUID = 6687178785122563992L;

	public XHashtable()
	{
		super();
	}

	public XHashtable(Map<K,V> V)
	{
		super();
		if(V!=null)
			putAll(V);
	}

	public synchronized void removeAll(Enumeration<K> E)
	{
		if(E!=null)
			for(;E.hasMoreElements();)
				remove(E.nextElement());
	}

	public synchronized void removeAll(Iterator<K> E)
	{
		if(E!=null)
			for(;E.hasNext();)
				remove(E.next());
	}

	public synchronized void removeAll(List<K> E)
	{
		if(E!=null)
			for(final K o : E)
				remove(o);
	}
}
