package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class HTTPclientIP extends StdWebMacro
{
	@Override public String name()	{return "HTTPclientIP";}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		if(httpReq.getClientAddress()!=null)
			return httpReq.getClientAddress().getHostAddress();
		return "Unknown";
	}
}
