package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class HTTPstatusInfo extends StdWebMacro
{
	@Override public String name()	{return "HTTPstatusInfo";}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		if(httpReq.getRequestObjects().get("SYSTEM_HTTP_STATUS_INFO")!=null)
			return (String)httpReq.getRequestObjects().get("SYSTEM_HTTP_STATUS_INFO");
		return "";
	}

}
