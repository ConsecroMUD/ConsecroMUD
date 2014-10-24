package com.suscipio_solutions.consecro_mud.core.collections;
import java.util.Map;

public class Pair<T,K> implements Map.Entry<T, K>
{
	public T first;
	public K second;
	public Pair(T frst, K scnd)
	{
		first=frst;
		second=scnd;
	}
	public static final class FirstConverter<T,K> implements Converter<Pair<T,K>,T>
	{
		@Override public T convert(Pair<T, K> obj) { return obj.first;}
	}
	public static final class SecondConverter<T,K> implements Converter<Pair<T,K>,K>
	{
		@Override public K convert(Pair<T, K> obj) { return obj.second;}
	}
	@Override public T getKey() { return first; }
	@Override public K getValue() { return second; }
	@Override public K setValue(K value) { second=value; return value; }
	@Override
	public boolean equals(Object o)
	{
		if(o==this) return true;
		if(o instanceof Pair)
		{
			@SuppressWarnings("rawtypes")
			final
			Pair p=(Pair)o;
			return ((p.first==first)||((p.first!=null)&&(p.first.equals(first))))
					&&((p.second==second)||((p.second!=null)&&(p.second.equals(second))));
		}
		return super.equals(o);
	}
	@Override
	public int hashCode()
	{
		return ((first==null)?0:first.hashCode()) ^ ((second==null)?0:second.hashCode());
	}
}
