package com.suscipio_solutions.consecro_mud.core.exceptions;


public class HTTPRedirectException extends HTTPServerException
{
	public static final long serialVersionUID=0;

	public HTTPRedirectException(String url)
	{
		super(url);
	}
}

