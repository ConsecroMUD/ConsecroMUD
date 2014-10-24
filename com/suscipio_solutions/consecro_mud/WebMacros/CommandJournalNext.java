package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.JournalsLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;
import com.suscipio_solutions.consecro_web.util.CWThread;


public class CommandJournalNext extends StdWebMacro
{
	@Override public String name() { return "CommandJournalNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		String last=httpReq.getUrlParameter("COMMANDJOURNAL");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("COMMANDJOURNAL");
			return "";
		}
		final MOB mob = Authenticate.getAuthenticatedMob(httpReq);
		String lastID="";
		boolean allJournals=false;
		if((Thread.currentThread() instanceof CWThread)
		&&CMath.s_bool(((CWThread)Thread.currentThread()).getConfig().getMiscProp("ADMIN"))
		&&parms.containsKey("ALLCOMMANDJOURNALS"))
			allJournals=true;
		for(final Enumeration<JournalsLibrary.CommandJournal> i=CMLib.journals().commandJournals();i.hasMoreElements();)
		{
			final JournalsLibrary.CommandJournal J=i.nextElement();
			final String name=J.NAME();
			if((last==null)
			||((last.length()>0)&&(last.equals(lastID))&&(!name.equals(lastID))))
			{
				if(allJournals||((mob!=null)&&(J.mask().length()>0)&&(!CMLib.masking().maskCheck(J.mask(),mob,true))))
				{
					httpReq.addFakeUrlParameter("COMMANDJOURNAL",name);
					return "";
				}
				last=name;
			}
			lastID=name;
		}
		httpReq.addFakeUrlParameter("COMMANDJOURNAL","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
