package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.JournalsLibrary;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.JournalsLibrary.ForumJournalFlags;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Resources;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;
import com.suscipio_solutions.consecro_web.util.CWThread;


public class ForumInfo extends StdWebMacro
{
	@Override public String name() { return "ForumInfo"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("JOURNAL");
		if(last==null)
			return " @break@";
		boolean securityOverride=false;
		if((Thread.currentThread() instanceof CWThread)
		&&CMath.s_bool(((CWThread)Thread.currentThread()).getConfig().getMiscProp("ADMIN"))
		&&parms.containsKey("ALLFORUMJOURNALS"))
			securityOverride=true;

		final MOB M = Authenticate.getAuthenticatedMob(httpReq);
		if((!securityOverride)&&(CMLib.journals().isImmortalJournalName(last))&&((M==null)||(!CMSecurity.isASysOp(M))))
			return " @break@";

		final Clan setClan=CMLib.clans().getClan(httpReq.getUrlParameter("CLAN"));
		final JournalsLibrary.ForumJournal journal=CMLib.journals().getForumJournal(last,setClan);
		if(journal == null)
			return " @break@";

		final StringBuffer str=new StringBuffer("");
		if(parms.containsKey("ISSMTPFORWARD"))
		{
			@SuppressWarnings("unchecked")
			final
			TreeMap<String, JournalsLibrary.SMTPJournal> set=(TreeMap<String, JournalsLibrary.SMTPJournal>) Resources.getResource("SYSTEM_SMTP_JOURNALS");
			final JournalsLibrary.SMTPJournal entry =(set!=null) ? set.get(last.toUpperCase().trim()) : null;
			final String email=((M!=null) &&(M.playerStats()!=null) && (M.playerStats().getEmail()!=null)) ? M.playerStats().getEmail() : "";
			str.append( ((entry!=null) && (email.length()>0)) ? Boolean.toString(entry.forward) : "false").append(", ");
		}

		if(parms.containsKey("ISSMTPSUBSCRIBER"))
		{
			final Map<String, List<String>> lists=Resources.getCachedMultiLists("mailinglists.txt",true);
			final List<String> mylist=lists.get(last);
			str.append( ((mylist!=null)&&(M!=null)) ? Boolean.toString(mylist.contains(M.Name())) : "false").append(", ");
		}

		if(parms.containsKey("SMTPADDRESS"))
		{
			@SuppressWarnings("unchecked")
			final
			TreeMap<String, JournalsLibrary.SMTPJournal> set=(TreeMap<String, JournalsLibrary.SMTPJournal>) Resources.getResource("SYSTEM_SMTP_JOURNALS");
			final JournalsLibrary.SMTPJournal entry =(set!=null) ? set.get(last.toUpperCase().trim()) : null;
			if((entry!=null)&&(entry.forward))
			{
				str.append( entry.name.replace(' ','_')+"@"+CMProps.getVar(CMProps.Str.MUDDOMAIN)).append(", ");
			}
		}

		if(parms.containsKey("CANADMIN")||parms.containsKey("ISADMIN"))
			str.append( ""+journal.authorizationCheck(M, ForumJournalFlags.ADMIN)).append(", ");

		if(parms.containsKey("CANPOST"))
			str.append( ""+journal.authorizationCheck(M, ForumJournalFlags.POST)).append(", ");

		if(parms.containsKey("CANREAD"))
			str.append( ""+journal.authorizationCheck(M, ForumJournalFlags.READ)).append(", ");

		if(parms.containsKey("CANREPLY"))
			str.append( ""+journal.authorizationCheck(M, ForumJournalFlags.REPLY)).append(", ");

		if(parms.containsKey("ADMINMASK"))
			str.append( ""+journal.adminMask()).append(", ");

		if(parms.containsKey("READMASK"))
			str.append( ""+journal.readMask()).append(", ");

		if(parms.containsKey("POSTMASK"))
			str.append( ""+journal.postMask()).append(", ");

		if(parms.containsKey("REPLYMASK"))
			str.append( ""+journal.replyMask()).append(", ");

		if(parms.containsKey("ID"))
			str.append( ""+journal.NAME()).append(", ");

		if(parms.containsKey("NAME"))
			str.append( ""+journal.NAME()).append(", ");

		if(parms.containsKey("EXPIRE"))
			str.append( "").append(", ");

		final JournalsLibrary.JournalSummaryStats stats = CMLib.journals().getJournalStats(journal);
		if(stats == null)
			return " @break@";

		if(parms.containsKey("POSTS"))
			str.append( ""+stats.posts).append(", ");

		if(parms.containsKey("THREADS"))
			str.append( ""+stats.threads).append(", ");

		if(parms.containsKey("SHORTDESC"))
			str.append( ""+stats.shortIntro).append(", ");

		if(parms.containsKey("LONGDESC"))
			str.append( ""+stats.longIntro).append(", ");

		if(parms.containsKey("IMAGEPATH"))
		{
			if((stats.imagePath==null)
			||(stats.imagePath.trim().length()==0))
				str.append( L("images/lilcm.jpg")).append(", ");
			else
				str.append( ""+stats.threads).append(", ");
		}

		String strstr=str.toString();
		if(strstr.endsWith(", "))
			strstr=strstr.substring(0,strstr.length()-2);
		return clearWebMacros(strstr);
	}
}
