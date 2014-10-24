package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class FactionName extends StdWebMacro
{
	@Override public String name() { return "FactionName"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("FACTION");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			final Faction F=CMLib.factions().getFaction(last);
			if(F!=null)
				return clearWebMacros(F.name());
		}
		return "";
	}
}
