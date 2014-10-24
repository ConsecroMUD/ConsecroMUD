package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings("rawtypes")
public class CharClassNext extends StdWebMacro
{
	@Override public String name() { return "CharClassNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("CLASS");
		final String base=httpReq.getUrlParameter("BASECLASS");
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
			&&((base==null)||(base.length()==0)||(C.baseClass().equalsIgnoreCase(base))))
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
