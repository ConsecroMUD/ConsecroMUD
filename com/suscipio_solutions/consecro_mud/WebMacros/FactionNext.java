package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings("rawtypes")
public class FactionNext extends StdWebMacro
{
	@Override public String name() { return "FactionNext"; }
	@Override public boolean isAdminMacro()   {return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("FACTION");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("FACTION");
			return "";
		}
		String lastID="";
		Faction F;
		String factionID;
		for(final Enumeration q=CMLib.factions().factions();q.hasMoreElements();)
		{
			F=(Faction)q.nextElement();
			factionID=F.factionID().toUpperCase().trim();
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!factionID.equalsIgnoreCase(lastID))))
			{
				httpReq.addFakeUrlParameter("FACTION",factionID);
				return "";
			}
			lastID=factionID;
		}
		httpReq.addFakeUrlParameter("FACTION","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
