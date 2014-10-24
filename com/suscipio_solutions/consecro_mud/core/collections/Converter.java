package com.suscipio_solutions.consecro_mud.core.collections;

public interface Converter<K, L>
{
	public L convert(K obj);

}
