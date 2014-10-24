package com.suscipio_solutions.consecro_mud.core.collections;

public class Triad<T,K,L> extends Pair<T,K>
{
	public L third;
	public Triad(T frst, K scnd, L thrd)
	{
		super(frst,scnd);
		third=thrd;
	}
	public static final class FirstConverter<T,K,L> implements Converter<Triad<T,K,L>,T>
	{
		@Override public T convert(Triad<T, K,L> obj) { return obj.first;}
	}
	public static final class SecondConverter<T,K,L> implements Converter<Triad<T,K,L>,K>
	{
		@Override public K convert(Triad<T, K, L> obj) { return obj.second;}
	}
	public static final class ThirdConverter<T,K,L> implements Converter<Triad<T,K,L>,L>
	{
		@Override public L convert(Triad<T, K, L> obj) { return obj.third;}
	}
	@Override
	public boolean equals(Object o)
	{
		if(o==this) return true;
		if(o instanceof Triad)
		{
			@SuppressWarnings("rawtypes")
			final
			Triad p=(Triad)o;
			return ((p.first==first)||((p.first!=null)&&(p.first.equals(first))))
					&&((p.second==second)||((p.second!=null)&&(p.second.equals(second))))
					&&((p.third==third)||((p.third!=null)&&(p.third.equals(third))));
		}
		return super.equals(o);
	}
	@Override
	public int hashCode()
	{
		return super.hashCode()  ^ ((third==null)?0:third.hashCode());
	}
}
