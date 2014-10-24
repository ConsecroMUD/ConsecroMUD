package com.suscipio_solutions.consecro_mud.WebMacros;

import java.net.URLEncoder;

import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class PollID extends StdWebMacro
{
	@Override public String name() { return "PollID"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("POLL");
		if(last==null) return " @break@";
		final java.util.Map<String,String> parms=parseParms(parm);
		try
		{
			if(parms.containsKey("ENCODED"))
				return URLEncoder.encode(last,"UTF-8");
		}
		catch(final Exception e) {}
		return clearWebMacros(last);
	}
}
