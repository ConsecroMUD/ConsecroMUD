package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;
import com.suscipio_solutions.consecro_web.util.CWConfig;
import com.suscipio_solutions.consecro_web.util.CWThread;


public class WebServerPort extends StdWebMacro
{
	@Override public String name()	{return "WebServerPort";}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		if(parms.containsKey("CURRENT"))
			return Integer.toString(httpReq.getClientPort());
		if(Thread.currentThread() instanceof CWThread)
		{
			final CWConfig config=((CWThread)Thread.currentThread()).getConfig();
			return CMParms.toStringList(config.getHttpListenPorts());
		}
		return Integer.toString(httpReq.getClientPort());
	}

}
