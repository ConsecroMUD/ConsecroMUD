package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class IsAccountSystem extends StdWebMacro
{
	@Override public String name() { return "IsAccountSystem"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		return ""+(CMProps.getIntVar(CMProps.Int.COMMONACCOUNTSYSTEM)>1);
	}
}
