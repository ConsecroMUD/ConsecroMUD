package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Deity;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class DeityID extends StdWebMacro
{
	@Override public String name() { return "DeityID"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("DEITY");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			final Deity D=CMLib.map().getDeity(last);
			if(D!=null)
				return clearWebMacros(D.Name());
		}
		return "";
	}
}
