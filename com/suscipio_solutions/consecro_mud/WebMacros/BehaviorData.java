package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class BehaviorData extends StdWebMacro
{
	@Override public String name() { return "BehaviorData"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("BEHAVIOR");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			final Behavior B=CMClass.getBehavior(last);
			if(B!=null)
			{
				final StringBuffer str=new StringBuffer("");
				if(parms.containsKey("HELP"))
				{
					StringBuilder s=CMLib.help().getHelpText("BEHAVIOR_"+B.ID(),null,true);
					if(s==null)	s=CMLib.help().getHelpText(B.ID(),null,true);
					int limit=78;
					if(parms.containsKey("LIMIT")) limit=CMath.s_int(parms.get("LIMIT"));
					str.append(helpHelp(s,limit));
				}
				String strstr=str.toString();
				if(strstr.endsWith(", "))
					strstr=strstr.substring(0,strstr.length()-2);
				return clearWebMacros(strstr);
			}
		}
		return "";
	}
}
