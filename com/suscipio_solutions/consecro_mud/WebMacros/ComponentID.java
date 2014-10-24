package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class ComponentID extends StdWebMacro
{
	@Override public String name() { return "ComponentID"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("COMPONENT");
		if(last==null) return " @break@";
		return last;
	}
}
