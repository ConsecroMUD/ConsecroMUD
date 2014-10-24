package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class AreaName extends StdWebMacro
{
	@Override public String name() { return "AreaName"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("AREA");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			final Area A=CMLib.map().getArea(last);
			if(A!=null)
				return clearWebMacros(A.Name());
		}
		return "";
	}
}
