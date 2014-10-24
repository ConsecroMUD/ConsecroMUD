package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class MUDServerPort extends StdWebMacro
{
	@Override public String name()	{return "MUDServerPort";}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		return CMProps.getVar(CMProps.Str.MUDPORTS);
	}

}
