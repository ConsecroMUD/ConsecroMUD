package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Iterator;

import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class ComponentNext extends StdWebMacro
{
	@Override public String name() { return "ComponentNext"; }
	@Override public boolean isAdminMacro()   {return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("COMPONENT");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("COMPONENT");
			return "";
		}
		String lastID="";
		String componentID;
		for(final Iterator<String> i=CMLib.ableMapper().getAbilityComponentMap().keySet().iterator();i.hasNext();)
		{
			componentID=i.next();
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!componentID.equalsIgnoreCase(lastID))))
			{
				httpReq.addFakeUrlParameter("COMPONENT",componentID);
				return "";
			}
			lastID=componentID;
		}
		httpReq.addFakeUrlParameter("COMPONENT","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
