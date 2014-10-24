package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Enumeration;

public class SingleEnumeration<K> implements Enumeration<K>
{
	private K k;

	public SingleEnumeration(K k)
	{
		this.k=k;
	}
	@Override public boolean hasMoreElements(){ return k!=null;}
	@Override public K nextElement(){ final K o=k; k=null; return o; }
}
