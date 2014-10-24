package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class MUDServerVersion extends StdWebMacro
{
	@Override public String name()	{return "MUDServerVersion";}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		return "ConsecroMUD-MainServer/" + CMProps.getVar(CMProps.Str.MUDVER);
	}

}
