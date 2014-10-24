package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class SocialID extends StdWebMacro
{
	@Override public String name() { return "SocialID"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("SOCIAL");
		if(last==null) return " @break@";
		return last;
	}
}
