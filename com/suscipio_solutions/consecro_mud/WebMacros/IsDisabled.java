package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class IsDisabled extends StdWebMacro
{
	@Override public String name() { return "IsDisabled"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final CMSecurity.DisFlag flag=(CMSecurity.DisFlag)CMath.s_valueOf(CMSecurity.DisFlag.class, parm.toUpperCase().trim());
		if(flag==null) return " @break@";
		return Boolean.toString(CMSecurity.isDisabled(flag));
	}
}
