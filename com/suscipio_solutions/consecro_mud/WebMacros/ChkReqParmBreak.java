package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class ChkReqParmBreak extends CheckReqParm
{
	@Override public String name() { return "ChkReqParmBreak"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String ans=super.runMacro(httpReq, parm);
		if(CMath.s_bool(ans))
			return " @break@";
		return "";
	}
}
