package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Iterator;

import com.suscipio_solutions.consecro_mud.core.Resources;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class ResourceMgr extends StdWebMacro
{
	@Override public String name() { return "ResourceMgr"; }
	@Override public boolean isAdminMacro()	{return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("RESOURCE");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("RESOURCE");
			return "";
		}
		else
		if(parms.containsKey("NEXT"))
		{
			String lastID="";
			for(final Iterator<String> k=Resources.findResourceKeys("");k.hasNext();)
			{
				final String key=k.next();
				if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!key.equals(lastID))))
				{
					httpReq.addFakeUrlParameter("RESOURCE",key);
					return "";
				}
				lastID=key;
			}
			httpReq.addFakeUrlParameter("RESOURCE","");
			if(parms.containsKey("EMPTYOK"))
				return "<!--EMPTY-->";
			return " @break@";
		}
		else
		if(parms.containsKey("DELETE"))
		{
			final String key=httpReq.getUrlParameter("RESOURCE");
			if((key!=null)&&(Resources.getResource(key)!=null))
			{
				Resources.removeResource(key);
				return "Resource '"+key+"' deleted.";
			}
			return "<!--EMPTY-->";
		}
		else
		if(last!=null)
			return last;
		return "<!--EMPTY-->";
	}

}
