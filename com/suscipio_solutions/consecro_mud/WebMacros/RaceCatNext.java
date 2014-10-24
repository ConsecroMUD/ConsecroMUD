package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;
import java.util.TreeSet;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings({"unchecked","rawtypes"})
public class RaceCatNext extends StdWebMacro
{
	@Override public String name() { return "RaceCatNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("RACECAT");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("RACECAT");
			return "";
		}
		Vector raceCats=new Vector();
		for(final Enumeration r=CMClass.races();r.hasMoreElements();)
		{
			final Race R=(Race)r.nextElement();
			if((!raceCats.contains(R.racialCategory()))
			&&((CMProps.isTheme(R.availabilityCode())&&(!CMath.bset(R.availabilityCode(),Area.THEME_SKILLONLYMASK)))
				||(parms.containsKey("ALL"))))
					raceCats.addElement(R.racialCategory());
		}
		raceCats=new Vector(new TreeSet(raceCats));
		String lastID="";
		for(final Enumeration r=raceCats.elements();r.hasMoreElements();)
		{
			final String RC=(String)r.nextElement();
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!RC.equals(lastID))))
			{
				httpReq.addFakeUrlParameter("RACECAT",RC);
				return "";
			}
			lastID=RC;
		}
		httpReq.addFakeUrlParameter("RACECAT","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
