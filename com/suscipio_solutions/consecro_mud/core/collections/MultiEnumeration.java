package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

public class MultiEnumeration<K> implements Enumeration<K>
{
	private final List<Enumeration<K>> enums=new Vector<Enumeration<K>>(3);
	private volatile int dex=0;
	private volatile Enumeration<K> enumer=null;

	@SuppressWarnings("unchecked")
	public MultiEnumeration(Enumeration<K>[] esets)
	{
		if((esets==null)||(esets.length==0))
			enums.add(EmptyEnumeration.INSTANCE);
		else
		for(final Enumeration<K> E : esets)
			if(E!=null) enums.add(E);
		setup();
	}

	public MultiEnumeration(Enumeration<K> eset)
	{
		enums.add(eset);
		setup();
	}

	public void addEnumeration(Enumeration<K> set)
	{
		if(set != null)
			enums.add(set);
		setup();
	}

	private void setup()
	{
		if((enumer==null)&&(dex<enums.size()))
			enumer=enums.get(dex);
		while((enumer!=null)&&(!enumer.hasMoreElements())&&(++dex<enums.size()))
			enumer=enums.get(dex);
	}

	@Override
	public boolean hasMoreElements()
	{
		if(enumer.hasMoreElements()) return true;
		while((!enumer.hasMoreElements())&&(++dex<enums.size()))
			enumer=enums.get(dex);
		return enumer.hasMoreElements();
	}

	@Override
	public K nextElement()
	{
		if(!hasMoreElements())
			throw new NoSuchElementException();
		return enumer.nextElement();
	}
}
