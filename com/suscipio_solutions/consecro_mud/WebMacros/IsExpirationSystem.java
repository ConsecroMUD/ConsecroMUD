package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class IsExpirationSystem extends StdWebMacro
{
	@Override public String name() { return "IsExpirationSystem"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		return ""+CMProps.getBoolVar(CMProps.Bool.ACCOUNTEXPIRATION);
	}
}
