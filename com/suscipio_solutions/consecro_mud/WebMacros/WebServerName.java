package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;
import com.suscipio_solutions.consecro_web.util.CWThread;


public class WebServerName extends StdWebMacro
{
	@Override public String name()	{return "WebServerName";}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		if(Thread.currentThread() instanceof CWThread)
		{
			final String fullThreadName=((CWThread)Thread.currentThread()).getName();
			final int x=fullThreadName.lastIndexOf('#');
			if(x>0)
				return fullThreadName.substring(0, x);
		}
		return "WebServer";
	}

}
