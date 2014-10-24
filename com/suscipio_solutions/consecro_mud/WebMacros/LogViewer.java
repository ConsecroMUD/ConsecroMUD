package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class LogViewer extends StdWebMacro
{
	@Override public String name() { return "LogViewer"; }
	@Override public boolean isAdminMacro()	{return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		String s=Log.instance().getLog().toString();
		s=CMStrings.replaceAll(s,"\n\r","\n");
		return clearWebMacros("<PRE>"+s+"</PRE>");
	}
}
