package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class EmptyEnumeration<K> implements Enumeration<K>
{
	@SuppressWarnings("rawtypes")
	public static final Enumeration INSTANCE=new EmptyEnumeration();

	public EmptyEnumeration(){}
	@Override public boolean hasMoreElements(){ return false;}
	@Override public K nextElement(){ throw new NoSuchElementException(); }
}
