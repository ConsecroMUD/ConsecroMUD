package com.suscipio_solutions.consecro_mud.WebMacros;

import java.net.URLEncoder;

import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class AreaScriptKey extends StdWebMacro
{
	@Override public String name() { return "AreaScriptKey"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("AREASCRIPT");
		if(last==null) return " @break@";
		final java.util.Map<String,String> parms=parseParms(parm);
		try
		{
			if(last.length()>0)
				if(parms.containsKey("ENCODED"))
					return URLEncoder.encode(clearWebMacros(last),"UTF-8");
				else
					return clearWebMacros(last);
		}
		catch(final Exception e){}
		return "";
	}
}
