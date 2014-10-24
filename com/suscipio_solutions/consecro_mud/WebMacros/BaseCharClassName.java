package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings("rawtypes")
public class BaseCharClassName extends StdWebMacro
{
	@Override public String name() { return "BaseCharClassName"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("BASECLASS");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			final java.util.Map<String,String> parms=parseParms(parm);
			CharClass C=CMClass.getCharClass(last);
			if(C!=null)
			{
				if(parms.containsKey("PLURAL"))
					return clearWebMacros(CMLib.english().makePlural(C.name()));
				else
					return clearWebMacros(C.name());
			}
			for(final Enumeration e=CMClass.charClasses();e.hasMoreElements();)
			{
				C=(CharClass)e.nextElement();
				if(C.baseClass().equalsIgnoreCase(last))
					if(parms.containsKey("PLURAL"))
						return clearWebMacros(CMLib.english().makePlural(C.baseClass()));
					else
						return clearWebMacros(C.baseClass());
			}
		}
		return "";
	}
}
