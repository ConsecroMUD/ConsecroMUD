package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class MUDServerStatus extends StdWebMacro
{
	@Override public String name()	{return "MUDServerStatus";}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		return CMProps.getVar(CMProps.Str.MUDSTATUS);
	}

}
