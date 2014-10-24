package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings("rawtypes")
public class BehaviorNext extends StdWebMacro
{
	@Override public String name() { return "BehaviorNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("BEHAVIOR");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("BEHAVIOR");
			return "";
		}
		String lastID="";
		for(final Enumeration b=CMClass.behaviors();b.hasMoreElements();)
		{
			final Behavior B=(Behavior)b.nextElement();
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!B.ID().equals(lastID))))
			{
				httpReq.addFakeUrlParameter("BEHAVIOR",B.ID());
				return "";
			}
			lastID=B.ID();
		}
		httpReq.addFakeUrlParameter("BEHAVIOR","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
