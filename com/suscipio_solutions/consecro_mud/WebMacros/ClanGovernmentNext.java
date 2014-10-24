package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.Common.interfaces.ClanGovernment;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class ClanGovernmentNext extends StdWebMacro
{
	@Override public String name() { return "ClanGovernmentNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("GOVERNMENT");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("GOVERNMENT");
			return "";
		}
		int lastID=-1;
		for(final ClanGovernment G : CMLib.clans().getStockGovernments())
		{
			if((last==null)||((last.length()>0)&&(CMath.s_int(last)==lastID)&&(G.getID()!=lastID)))
			{
				httpReq.addFakeUrlParameter("GOVERNMENT",Integer.toString(G.getID()));
				return "";
			}
			lastID=G.getID();
		}
		httpReq.addFakeUrlParameter("GOVERNMENT","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
