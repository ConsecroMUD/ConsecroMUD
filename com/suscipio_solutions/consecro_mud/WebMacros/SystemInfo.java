package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class SystemInfo extends StdWebMacro
{
	@Override public String name() { return "SystemInfo"; }
	@Override public boolean isAdminMacro()	{return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final StringBuffer str=new StringBuffer("");
		final java.util.Map<String,String> parms=parseParms(parm);
		for(final String key : parms.keySet())
		{
			if(key.length()>0)
			{
				String answer=CMLib.threads().tickInfo(key);
				if(answer.length()==0)
					answer=CMLib.threads().systemReport(key);
				str.append(answer+", ");
			}
		}
		String strstr=str.toString();
		if(strstr.endsWith(", "))
			strstr=strstr.substring(0,strstr.length()-2);
		return clearWebMacros(strstr);
	}
}
