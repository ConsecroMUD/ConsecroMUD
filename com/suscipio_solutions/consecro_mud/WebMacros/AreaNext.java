package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.SpaceObject;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;




@SuppressWarnings("rawtypes")
public class AreaNext extends StdWebMacro
{
	@Override public String name() { return "AreaNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		String last=httpReq.getUrlParameter("AREA");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("AREA");
			return "";
		}
		final boolean all=parms.containsKey("SPACE")||parms.containsKey("ALL");
		String lastID="";
		for(final Enumeration a=CMLib.map().areas();a.hasMoreElements();)
		{
			final Area A=(Area)a.nextElement();
			if((!(A instanceof SpaceObject))||all)
			{
				if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!A.Name().equals(lastID))))
				{
					httpReq.addFakeUrlParameter("AREA",A.Name());
					if((!CMLib.flags().isHidden(A))&&(!CMath.bset(A.flags(),Area.FLAG_INSTANCE_CHILD)))
						return "";
					last=A.Name();
				}
				lastID=A.Name();
			}
		}
		httpReq.addFakeUrlParameter("AREA","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
