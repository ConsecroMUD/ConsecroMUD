package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Iterator;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Poll;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class PollNext extends StdWebMacro
{
	@Override public String name() { return "PollNext"; }
	@Override public boolean isAdminMacro()   {return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("POLL");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("POLL");
			return "";
		}
		String lastID="";
		for(final Iterator<Poll> q=CMLib.polls().getPollList();q.hasNext();)
		{
			final Poll poll=q.next();
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!poll.getName().equalsIgnoreCase(lastID))))
			{
				httpReq.addFakeUrlParameter("POLL",poll.getName());
				return "";
			}
			lastID=poll.getName();
		}
		httpReq.addFakeUrlParameter("POLL","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
