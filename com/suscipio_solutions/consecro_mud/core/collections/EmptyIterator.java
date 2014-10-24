package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

@SuppressWarnings("rawtypes")
public class EmptyIterator<K> implements Iterator<K>
{
	private EmptyIterator(){}
	@Override public boolean hasNext(){ return false;}
	@Override public K next(){ throw new NoSuchElementException(); }
	@Override public void remove() { throw new NoSuchElementException();}
	public static final Iterator INSTANCE=new EmptyIterator();
	public static final Iterator<String> STRINSTANCE=new EmptyIterator<String>();
}
