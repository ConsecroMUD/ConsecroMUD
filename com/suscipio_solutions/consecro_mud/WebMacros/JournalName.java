package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class JournalName extends StdWebMacro
{
	@Override public String name() { return "JournalName"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("JOURNAL");
		final java.util.Map<String,String> parms=parseParms(parm);
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			final boolean webify=parms.containsKey("WEBCOLOR");
			final boolean decolor=parms.containsKey("NOCOLOR");
			StringBuffer lastBuf=new StringBuffer(last);
			if(webify)
				lastBuf=super.colorwebifyOnly(lastBuf);
			if(decolor)
				lastBuf=new StringBuffer(CMStrings.removeColors(lastBuf.toString()));
			return clearWebMacros(lastBuf.toString());
		}
		return "";
	}
}
