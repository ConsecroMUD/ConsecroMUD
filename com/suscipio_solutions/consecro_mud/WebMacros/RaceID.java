package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class RaceID extends StdWebMacro
{
	@Override public String name() { return "RaceID"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("RACE");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			final Race R=CMClass.getRace(last);
			if(R!=null)
				return clearWebMacros(R.ID());
		}
		return "";
	}
}
