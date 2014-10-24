package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings("rawtypes")
public class ClanNext extends StdWebMacro
{
	@Override public String name() { return "ClanNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("CLAN");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("CLAN");
			return "";
		}
		String lastID="";
		for(final Enumeration c=CMLib.clans().clans();c.hasMoreElements();)
		{
			final Clan C=(Clan)c.nextElement();
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!C.clanID().equals(lastID))))
			{
				httpReq.addFakeUrlParameter("CLAN",C.clanID());
				return "";
			}
			lastID=C.clanID();
		}
		httpReq.addFakeUrlParameter("CLAN","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
