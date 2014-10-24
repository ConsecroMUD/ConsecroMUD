package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings("rawtypes")
public class AutoTitleNext extends StdWebMacro
{
	@Override public String name() { return "AutoTitleNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("AUTOTITLE");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("AUTOTITLE");
			return "";
		}
		String lastID="";
		for(final Enumeration r=CMLib.titles().autoTitles();r.hasMoreElements();)
		{
			final String title=(String)r.nextElement();
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!title.equals(lastID))))
			{
				httpReq.addFakeUrlParameter("AUTOTITLE",title);
				return "";
			}
			lastID=title;
		}
		httpReq.addFakeUrlParameter("AUTOTITLE","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
