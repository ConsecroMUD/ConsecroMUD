package com.suscipio_solutions.consecro_mud.WebMacros;

import java.net.URLEncoder;

import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class AutoTitleID extends StdWebMacro
{
	@Override public String name() { return "AutoTitleID"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("AUTOTITLE");
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
