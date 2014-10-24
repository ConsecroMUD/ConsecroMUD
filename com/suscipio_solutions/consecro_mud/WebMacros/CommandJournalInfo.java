package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.JournalsLibrary;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class CommandJournalInfo extends StdWebMacro
{
	@Override public String name() { return "CommandJournalInfo"; }

	@Override public boolean isAdminMacro() { return true; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("COMMANDJOURNAL");
		final StringBuffer str=new StringBuffer("");
		if(parms.containsKey("ALLFLAGS"))
		{
			for(final JournalsLibrary.CommandJournalFlags flag : JournalsLibrary.CommandJournalFlags.values())
				str.append("FLAG_"+flag.name()).append(", ");
		}
		else
		if(last==null)
			return " @break@";
		if(last.length()>0)
		{
			final JournalsLibrary.CommandJournal C=CMLib.journals().getCommandJournal(last);
			if(C==null) return " @break@";
			if(parms.containsKey("ID"))
				str.append(C.NAME()).append(", ");
			if(parms.containsKey("NAME"))
				str.append(C.NAME()).append(", ");
			if(parms.containsKey("JOURNALNAME"))
				str.append(C.JOURNAL_NAME()).append(", ");
			if(parms.containsKey("MASK"))
				str.append(C.mask()).append(", ");
			if(parms.containsKey("FLAGSET"))
				for(final JournalsLibrary.CommandJournalFlags flag : JournalsLibrary.CommandJournalFlags.values())
					httpReq.addFakeUrlParameter("FLAG_"+flag.name(), C.getFlag(flag)!=null?((C.getFlag(flag).length()==0)?"on":C.getFlag(flag)):"");
			for(final JournalsLibrary.CommandJournalFlags flag : JournalsLibrary.CommandJournalFlags.values())
				if(parms.containsKey("FLAG_"+flag.name().toUpperCase().trim()))
					str.append(C.getFlag(flag)!=null?((C.getFlag(flag).length()==0)?"on":C.getFlag(flag)):"").append(", ");
		}
		String strstr=str.toString();
		if(strstr.endsWith(", "))
			strstr=strstr.substring(0,strstr.length()-2);
		return clearWebMacros(strstr);
	}
}
