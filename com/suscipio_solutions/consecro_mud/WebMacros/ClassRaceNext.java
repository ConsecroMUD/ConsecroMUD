package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings("rawtypes")
public class ClassRaceNext extends StdWebMacro
{
	@Override public String name() { return "ClassRaceNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String cclass=httpReq.getUrlParameter("CLASS");
		if(cclass.trim().length()==0) return " @break@";
		final CharClass C=CMClass.getCharClass(cclass.trim());
		if(C==null) return " @break";
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
			if(((CMProps.isTheme(R.availabilityCode())&&(!CMath.bset(R.availabilityCode(),Area.THEME_SKILLONLYMASK)))
				||(parms.containsKey("ALL")))
			&&(CMStrings.containsIgnoreCase(C.getRequiredRaceList(),"All")
				||CMStrings.containsIgnoreCase(C.getRequiredRaceList(),R.ID())
				||CMStrings.containsIgnoreCase(C.getRequiredRaceList(),R.name())
				||CMStrings.containsIgnoreCase(C.getRequiredRaceList(),R.racialCategory())))
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
