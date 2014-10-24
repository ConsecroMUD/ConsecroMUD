package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class SocialNext extends StdWebMacro
{
	@Override public String name() { return "SocialNext"; }
	@Override public boolean isAdminMacro()   {return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("SOCIAL");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("SOCIAL");
			return "";
		}
		String lastID="";
		for(int s=0;s<CMLib.socials().getSocialsList().size();s++)
		{
			final String name=CMLib.socials().getSocialsList().get(s);
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!name.equalsIgnoreCase(lastID))))
			{
				httpReq.addFakeUrlParameter("SOCIAL",name);
				return "";
			}
			lastID=name;
		}
		httpReq.addFakeUrlParameter("SOCIAL","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
