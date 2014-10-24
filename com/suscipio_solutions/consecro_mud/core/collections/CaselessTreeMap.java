package com.suscipio_solutions.consecro_mud.core.collections;

import java.util.Comparator;
import java.util.TreeMap;

public class CaselessTreeMap<K> extends TreeMap<String,K>
{
	private static final long serialVersionUID = 5949532522375107316L;
	public CaselessTreeMap()
	{
		super(new Comparator<String>()
		{
			@Override
			public int compare(String arg0, String arg1)
			{
				return arg0.compareToIgnoreCase(arg1);
			}
		});
	}
}
