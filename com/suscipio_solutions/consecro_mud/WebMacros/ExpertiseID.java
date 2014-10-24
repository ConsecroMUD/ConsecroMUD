package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ExpertiseLibrary;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class ExpertiseID extends StdWebMacro
{
	@Override public String name() { return "ExpertiseID"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("EXPERTISE");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			final ExpertiseLibrary.ExpertiseDefinition E=CMLib.expertises().getDefinition(last);
			if(E!=null) return E.ID;
		}
		return "";
	}
}
