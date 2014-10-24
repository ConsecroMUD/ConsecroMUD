package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.JournalsLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class JournalMessageNext extends StdWebMacro
{
	@Override public String name() { return "JournalMessageNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String journalName=httpReq.getUrlParameter("JOURNAL");
		if(journalName==null)
			return " @break@";

		if(CMLib.journals().isImmortalJournalName(journalName))
		{
			final MOB M = Authenticate.getAuthenticatedMob(httpReq);
			if((M==null)||(!CMSecurity.isASysOp(M)))
				return " @break@";
		}

		String srch=httpReq.getUrlParameter("JOURNALMESSAGESEARCH");
		if(srch!=null)
			srch=srch.toLowerCase();
		String last=httpReq.getUrlParameter("JOURNALMESSAGE");
		int cardinal=CMath.s_int(httpReq.getUrlParameter("JOURNALCARDINAL"));
		if(parms.containsKey("RESET"))
		{
			if(last!=null)
			{
				httpReq.removeUrlParameter("JOURNALMESSAGE");
				httpReq.removeUrlParameter("JOURNALCARDINAL");
			}
			return "";
		}
		final MOB M = Authenticate.getAuthenticatedMob(httpReq);
		cardinal++;
		JournalsLibrary.JournalEntry entry = null;
		final String page=httpReq.getUrlParameter("JOURNALPAGE");
		final String mpage=httpReq.getUrlParameter("MESSAGEPAGE");
		final String parent=httpReq.getUrlParameter("JOURNALPARENT");
		final String dbsearch=httpReq.getUrlParameter("DBSEARCH");
		final Clan setClan=CMLib.clans().getClan(httpReq.getUrlParameter("CLAN"));
		final JournalsLibrary.ForumJournal journal= CMLib.journals().getForumJournal(journalName,setClan);
		final List<JournalsLibrary.JournalEntry> msgs=JournalInfo.getMessages(journalName,journal,page,mpage,parent,dbsearch,httpReq.getRequestObjects());
		while((entry==null)||(!CMLib.journals().canReadMessage(entry,srch,M,parms.containsKey("NOPRIV"))))
		{
			entry = JournalInfo.getNextEntry(msgs,last);
			if(entry==null)
			{
				httpReq.addFakeUrlParameter("JOURNALMESSAGE","");
				if(parms.containsKey("EMPTYOK"))
					return "<!--EMPTY-->";
				return " @break@";
			}
			last=entry.key;
		}
		entry.cardinal=cardinal;
		httpReq.addFakeUrlParameter("JOURNALCARDINAL",""+cardinal);
		httpReq.addFakeUrlParameter("JOURNALMESSAGE",last);
		return "";
	}
}
