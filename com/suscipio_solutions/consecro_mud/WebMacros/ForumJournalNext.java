package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.JournalsLibrary;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.JournalsLibrary.ForumJournal;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;
import com.suscipio_solutions.consecro_web.util.CWThread;


@SuppressWarnings({"unchecked","rawtypes"})
public class ForumJournalNext extends StdWebMacro
{
	@Override public String name() { return "ForumJournalNext"; }

	public static MOB guestM = null;

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("JOURNAL");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("JOURNAL");
			httpReq.getRequestObjects().remove("JOURNALLIST");
			return "";
		}
		MOB M = Authenticate.getAuthenticatedMob(httpReq);
		if(M==null)
		{
			if(guestM==null)
			{
				guestM=CMClass.getFactoryMOB();
				guestM.basePhyStats().setLevel(0);
				guestM.phyStats().setLevel(0);
				guestM.setName(("guest"));
			}
			M=guestM;
		}

		final Clan setClan=CMLib.clans().getClan(httpReq.getUrlParameter("CLAN"));
		List<String> journals;
		if((setClan!=null)&&(CMLib.journals().getClanForums(setClan)!=null))
		{
			journals=(List<String>)httpReq.getRequestObjects().get("JOURNALLIST_FOR_"+setClan.clanID());
			if(journals==null)
			{
				journals=new Vector<String>();
				final List<JournalsLibrary.ForumJournal> clanForumJournals=CMLib.journals().getClanForums(setClan);
				for (final ForumJournal CJ : clanForumJournals)
				{
					if((!journals.contains(CJ.NAME().toUpperCase()))
					&&(CMLib.masking().maskCheck(CJ.readMask(), M, true)))
						journals.add(CJ.NAME());
				}
				httpReq.getRequestObjects().put("JOURNALLIST_FOR_"+setClan.clanID(),journals);
			}
		}
		else
		{
			journals=(List<String>)httpReq.getRequestObjects().get("JOURNALLIST");
			if(journals==null)
			{
				journals=new Vector();
				for(final Enumeration<JournalsLibrary.ForumJournal> e=CMLib.journals().forumJournals();e.hasMoreElements();)
				{
					final JournalsLibrary.ForumJournal CJ=e.nextElement();
					if((!journals.contains(CJ.NAME().toUpperCase()))
					&&(CMLib.masking().maskCheck(CJ.readMask(), M, true)))
						journals.add(CJ.NAME());
				}
				httpReq.getRequestObjects().put("JOURNALLIST",journals);
			}
		}
		String lastID="";
		final HashSet<String> H=CMLib.journals().getImmortalJournalNames();
		boolean allForumJournals=false;
		if((Thread.currentThread() instanceof CWThread)
		&&CMath.s_bool(((CWThread)Thread.currentThread()).getConfig().getMiscProp("ADMIN"))
		&&parms.containsKey("ALLFORUMJOURNALS"))
			allForumJournals=true;

		for(int j=0;j<journals.size();j++)
		{
			final String B=journals.get(j);
			if((!allForumJournals)&&(H.contains(B.toUpperCase().trim()))&&((M==null)||(!CMSecurity.isASysOp(M))))
				continue;
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!B.equals(lastID))))
			{
				httpReq.addFakeUrlParameter("JOURNAL",B);
				return "";
			}
			lastID=B;
		}
		httpReq.addFakeUrlParameter("JOURNAL","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
