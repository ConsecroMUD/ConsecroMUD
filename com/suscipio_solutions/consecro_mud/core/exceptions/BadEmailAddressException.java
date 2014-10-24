package com.suscipio_solutions.consecro_mud.core.exceptions;


public class BadEmailAddressException extends CMudException
{
	public static final long serialVersionUID=0;
	public BadEmailAddressException(String s)
	{
		super(s);
	}
}
