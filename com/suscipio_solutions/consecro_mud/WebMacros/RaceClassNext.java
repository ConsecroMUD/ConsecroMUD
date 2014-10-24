package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings("rawtypes")
public class RaceClassNext extends StdWebMacro
{
	@Override public String name() { return "RaceClassNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String race=httpReq.getUrlParameter("RACE");
		if(race.length()==0) return " @break@";
		final Race R=CMClass.getRace(race);
		final String last=httpReq.getUrlParameter("CLASS");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("CLASS");
			return "";
		}
		String lastID="";
		for(final Enumeration c=CMClass.charClasses();c.hasMoreElements();)
		{
			final CharClass C=(CharClass)c.nextElement();
			if(((CMProps.isTheme(C.availabilityCode()))||(parms.containsKey("ALL")))
				&&(CMStrings.containsIgnoreCase(C.getRequiredRaceList(),"All")
					||CMStrings.containsIgnoreCase(C.getRequiredRaceList(),R.ID())
					||CMStrings.containsIgnoreCase(C.getRequiredRaceList(),R.name())
					||CMStrings.containsIgnoreCase(C.getRequiredRaceList(),R.racialCategory())))
			{
				if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!C.ID().equals(lastID))))
				{
					httpReq.addFakeUrlParameter("CLASS",C.ID());
					return "";
				}
				lastID=C.ID();
			}
		}
		httpReq.addFakeUrlParameter("CLASS","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
