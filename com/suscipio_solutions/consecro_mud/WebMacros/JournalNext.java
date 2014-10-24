package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.JournalsLibrary.CommandJournal;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings({"unchecked","rawtypes"})
public class JournalNext extends StdWebMacro
{
	@Override public String name() { return "JournalNext"; }

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

		List<String> journals=(List<String>)httpReq.getRequestObjects().get("JOURNALLIST");
		if(journals==null)
		{
			final List<String> rawJournals=CMLib.database().DBReadJournals();
			if(!rawJournals.contains("SYSTEM_NEWS"))
				rawJournals.add("SYSTEM_NEWS");
			for(final Enumeration e=CMLib.journals().commandJournals();e.hasMoreElements();)
			{
				final CommandJournal CJ=(CommandJournal)e.nextElement();
				if((!rawJournals.contains(CJ.NAME().toUpperCase()))
				&&(!rawJournals.contains(CJ.JOURNAL_NAME())))
					rawJournals.add(CJ.JOURNAL_NAME());
			}
			Collections.sort(rawJournals);
			journals=new Vector<String>();
			String s;
			for(final Iterator<String> i=rawJournals.iterator();i.hasNext();)
			{
				s=i.next();
				if(s.startsWith("SYSTEM_"))
				{
					journals.add(s);
					i.remove();
				}
			}
			journals.addAll(rawJournals);
			httpReq.getRequestObjects().put("JOURNALLIST",journals);
		}
		String lastID="";
		final HashSet<String> H=CMLib.journals().getImmortalJournalNames();
		final MOB M = Authenticate.getAuthenticatedMob(httpReq);
		for(int j=0;j<journals.size();j++)
		{
			final String B=journals.get(j);
			if((H.contains(B.toUpperCase().trim()))&&((M==null)||(!CMSecurity.isASysOp(M))))
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
