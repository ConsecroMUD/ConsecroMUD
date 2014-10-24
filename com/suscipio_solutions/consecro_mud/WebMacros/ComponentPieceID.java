package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class ComponentPieceID extends StdWebMacro
{
	@Override public String name() { return "ComponentPieceID"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("COMPONENTPIECE");
		if(last==null) return " @break@";
		return last;
	}
}
