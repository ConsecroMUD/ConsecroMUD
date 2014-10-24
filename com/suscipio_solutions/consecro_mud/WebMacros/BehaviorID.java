package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class BehaviorID extends StdWebMacro
{
	@Override public String name() { return "BehaviorID"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("BEHAVIOR");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			final Behavior B=CMClass.getBehavior(last);
			if(B!=null)
				return B.ID();
		}
		return "";
	}
}
