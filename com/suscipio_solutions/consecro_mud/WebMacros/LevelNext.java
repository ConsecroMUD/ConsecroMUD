package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class LevelNext extends StdWebMacro
{
	@Override public String name() { return "LevelNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("LEVEL");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("LEVEL");
			return "";
		}
		int lastLevel=CMProps.getIntVar(CMProps.Int.LASTPLAYERLEVEL);
		for(final String key : parms.keySet())
		{
			if(CMath.isInteger(key))
				lastLevel=CMath.s_int(key);
		}
		if((last==null)||(last.length()>0))
		{
			int level=0;
			if(last!=null) level=CMath.s_int(last);
			level++;
			if(level<=lastLevel)
			{
				httpReq.addFakeUrlParameter("LEVEL",""+level);
				return "";
			}
		}
		httpReq.addFakeUrlParameter("LEVEL","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
