package com.suscipio_solutions.consecro_mud.core.collections;

public interface Filterer<K>
{
	public boolean passesFilter(K obj);

	@SuppressWarnings("rawtypes")
	public static final Filterer ANYTHING=new Filterer()
	{
		@Override public boolean passesFilter(Object obj) { return true; }
	};
}
