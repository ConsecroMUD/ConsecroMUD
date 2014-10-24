package com.suscipio_solutions.consecro_mud.core.exceptions;




public class CMException extends CMudException
{
	static final long serialVersionUID=0;

	public CMException(String s)
	{
		super(s);
	}
	public CMException(String s, Exception e)
	{
		super(s,e);
	}

}
