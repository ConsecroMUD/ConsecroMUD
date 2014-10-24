package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class LevelNumber extends StdWebMacro
{
	@Override public String name() { return "LevelNumber"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("LEVEL");
		if(last==null) return " @break@";
		if(last.length()>0)
			return clearWebMacros(last);
		return "";
	}
}
