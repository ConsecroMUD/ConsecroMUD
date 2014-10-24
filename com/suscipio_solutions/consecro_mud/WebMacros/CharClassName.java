package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class CharClassName extends StdWebMacro
{
	@Override public String name() { return "CharClassName"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("CLASS");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			final CharClass C=CMClass.getCharClass(last);
			if(C!=null)
				return clearWebMacros(C.name());
		}
		return "";
	}
}
