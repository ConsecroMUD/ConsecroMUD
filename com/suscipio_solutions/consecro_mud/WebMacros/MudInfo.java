package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class MudInfo extends StdWebMacro
{
	@Override public String name() { return "MudInfo"; }
	@Override public boolean isAdminMacro()   {return false;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		if(parms.containsKey("DOMAIN"))
			return CMProps.getVar(CMProps.Str.MUDDOMAIN);
		if(parms.containsKey("EMAILOK"))
			return ""+(CMProps.getVar(CMProps.Str.MAILBOX).length()>0);
		if(parms.containsKey("MAILBOX"))
			return CMProps.getVar(CMProps.Str.MAILBOX);
		if(parms.containsKey("NAME"))
			return CMProps.getVar(CMProps.Str.MUDNAME);
		if(parms.containsKey("CHARSET"))
			return CMProps.getVar(CMProps.Str.CHARSETOUTPUT);
		if(parms.containsKey("PORT"))
		{
			String ports=CMProps.getVar(CMProps.Str.MUDPORTS);
			if(ports==null) return "Booting";
			ports=ports.trim();
			final int x=ports.indexOf(' ');
			if(x<0)
				return clearWebMacros(ports);
			return clearWebMacros(ports.substring(0,x));
		}
		return "";
	}
}
