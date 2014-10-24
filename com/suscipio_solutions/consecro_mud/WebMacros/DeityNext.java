package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;
import java.util.HashSet;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Deity;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings({"unchecked","rawtypes"})
public class DeityNext extends StdWebMacro
{
	@Override public String name() { return "DeityNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("DEITY");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("DEITY");
			return "";
		}
		String lastID="";
		final HashSet heavensfound=new HashSet();
		for(final Enumeration d=CMLib.map().deities();d.hasMoreElements();)
		{
			final Deity D=(Deity)d.nextElement();
			if((D.location()!=null)&&(!heavensfound.contains(D.location())))
			{
				if(parms.containsKey("HEAVENS"))
					heavensfound.add(D.location());
				if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!D.Name().equals(lastID))))
				{
					httpReq.addFakeUrlParameter("DEITY",D.Name());
					return "";
				}
				lastID=D.Name();
			}
		}
		httpReq.addFakeUrlParameter("DEITY","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
