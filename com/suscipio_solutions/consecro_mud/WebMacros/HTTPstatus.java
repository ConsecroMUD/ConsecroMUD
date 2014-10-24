package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class HTTPstatus extends StdWebMacro
{
	@Override public String name()	{return "HTTPstatus";}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		if(httpReq.getRequestObjects().get("SYSTEM_HTTP_STATUS")!=null)
			return (String)httpReq.getRequestObjects().get("SYSTEM_HTTP_STATUS");
		return "";
	}

}
