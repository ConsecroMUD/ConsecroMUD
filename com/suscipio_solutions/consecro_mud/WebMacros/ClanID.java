package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class ClanID extends StdWebMacro
{
	@Override public String name() { return "ClanID"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("CLAN");
		final java.util.Map<String,String> parms=parseParms(parm);
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			final boolean webify=parms.containsKey("WEBCOLOR");
			final boolean decolor=parms.containsKey("NOCOLOR");
			final Clan C=CMLib.clans().getClan(last);
			if(C!=null)
			{
				StringBuffer clanId=new StringBuffer(C.clanID());
				if(webify)
					clanId=super.colorwebifyOnly(clanId);
				if(decolor)
					clanId=new StringBuffer(CMStrings.removeColors(clanId.toString()));
				return clearWebMacros(clanId.toString());
			}
		}
		return "";
	}
}
