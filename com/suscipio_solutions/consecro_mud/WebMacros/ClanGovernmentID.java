package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.Common.interfaces.ClanGovernment;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class ClanGovernmentID extends StdWebMacro
{
	@Override public String name() { return "ClanGovernmentID"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("GOVERNMENT");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			if(CMath.isInteger(last))
			{
				final ClanGovernment G=CMLib.clans().getStockGovernment(CMath.s_int(last));
				if(G!=null)
					return clearWebMacros(Integer.toString(G.getID()));
			}
			return clearWebMacros(last);
		}
		return "";
	}
}
