package com.suscipio_solutions.consecro_mud.core.exceptions;

public abstract class CMudException extends Exception
{
	private static final long serialVersionUID = 8932995125810826091L;

	public CMudException(String s)
	{
		super(s,new Exception());
	}
	public CMudException(String s, Exception e)
	{
		super(s,e);
	}
}

