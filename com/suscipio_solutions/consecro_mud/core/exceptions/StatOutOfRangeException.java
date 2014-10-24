package com.suscipio_solutions.consecro_mud.core.exceptions;




public class StatOutOfRangeException extends CMudException
{
	static final long serialVersionUID=0;

	public StatOutOfRangeException(String s)
	{
		super(s);
	}
	public StatOutOfRangeException(String s, Exception e)
	{
		super(s,e);
	}

}
