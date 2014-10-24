package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;
import com.suscipio_solutions.consecro_web.util.CWThread;


public class WebServerVersion extends StdWebMacro
{
	@Override public String name()	{return "WebServerVersion";}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		if(Thread.currentThread() instanceof CWThread)
			return "WebServer "+((CWThread)Thread.currentThread()).getConfig().getCoffeeWebServer().getVersion();
		return Float.toString(httpReq.getHttpVer());
	}

}
