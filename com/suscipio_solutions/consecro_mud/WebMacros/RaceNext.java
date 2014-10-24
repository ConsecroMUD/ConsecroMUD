package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings("rawtypes")
public class RaceNext extends StdWebMacro
{
	@Override public String name() { return "RaceNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("RACE");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("RACE");
			return "";
		}
		String lastID="";
		for(final Enumeration r=CMClass.races();r.hasMoreElements();)
		{
			final Race R=(Race)r.nextElement();
			if((CMProps.isTheme(R.availabilityCode())&&(!CMath.bset(R.availabilityCode(),Area.THEME_SKILLONLYMASK)))
			||(parms.containsKey("ALL")))
			{
				if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!R.ID().equals(lastID))))
				{
					httpReq.addFakeUrlParameter("RACE",R.ID());
					return "";
				}
				lastID=R.ID();
			}
		}
		httpReq.addFakeUrlParameter("RACE","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
