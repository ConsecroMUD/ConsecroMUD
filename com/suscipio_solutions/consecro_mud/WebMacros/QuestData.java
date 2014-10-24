package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Quest;
import com.suscipio_solutions.consecro_mud.core.CMFile;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.Resources;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class QuestData extends StdWebMacro
{
	@Override public String name() { return "QuestData"; }
	@Override public boolean isAdminMacro()	{return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("QUEST");
		if(last==null) return "";
		if(last.length()>0)
		{
			Quest Q=CMLib.quests().fetchQuest(last);
			if(Q==null)
			{
				final String newLast=CMStrings.replaceAll(last,"*","@");
				for(int q=0;q<CMLib.quests().numQuests();q++)
					if((""+CMLib.quests().fetchQuest(q)).equals(newLast))
					{ Q=CMLib.quests().fetchQuest(q); break;}
			}
			if(Q==null) return "";
			if(parms.containsKey("NAME"))
				return clearWebMacros(Q.name());
			if(parms.containsKey("ID"))
				return clearWebMacros(CMStrings.replaceAll(""+Q,"@","*"));
			if(parms.containsKey("DURATION"))
				return ""+Q.duration();
			if(parms.containsKey("WAIT"))
				return ""+Q.minWait();
			if(parms.containsKey("INTERVAL"))
				return ""+Q.waitInterval();
			if(parms.containsKey("RUNNING"))
				return ""+Q.running();
			if(parms.containsKey("WAITING"))
				return ""+Q.waiting();
			if(parms.containsKey("SUSPENDED"))
				return ""+Q.suspended();
			if(parms.containsKey("REMAINING"))
				return ""+Q.minsRemaining();
			if(parms.containsKey("REMAININGLEFT"))
			{
				if(Q.duration()==0)
					return "eternity";
				return Q.minsRemaining()+" minutes";
			}
			if(parms.containsKey("WAITLEFT"))
				return ""+Q.waitRemaining();
			if(parms.containsKey("WAITMINSLEFT"))
			{
				long min=Q.waitRemaining();
				if(min>0)
				{
					min=min*CMProps.getTickMillis();
					if(min>60000)
						return (min/60000)+" minutes";
					return (min/1000)+" seconds";
				}
				return min+" minutes";
			}
			if(parms.containsKey("WINNERS"))
				return ""+Q.getWinnerStr();
			if(parms.containsKey("RAWTEXT"))
			{
				StringBuffer script=new StringBuffer(Q.script());
				if((parms.containsKey("REDIRECT"))
				&&(script.toString().toUpperCase().trim().startsWith("LOAD=")))
				{
					final String fileName=script.toString().trim().substring(5);
					final CMFile F=new CMFile(Resources.makeFileResourceName(fileName),null,CMFile.FLAG_LOGERRORS);
					if((F.exists())&&(F.canRead()))
						script=F.text();
					script=new StringBuffer(CMStrings.replaceAll(script.toString(),"\n\r","\n"));
				}
				script=new StringBuffer(CMStrings.replaceAll(script.toString(),"&","&amp;"));
				String postFix="";
				final int limit=script.toString().toUpperCase().indexOf("<?XML");
				if(limit>=0)
				{
					postFix=script.toString().substring(limit);
					script=new StringBuffer(script.toString().substring(0,limit));
				}
				for(int i=0;i<script.length();i++)
					if((script.charAt(i)==';')
					&&((i==0)||(script.charAt(i-1)!='\\')))
						script.setCharAt(i,'\n');
				script=new StringBuffer(CMStrings.replaceAll(script.toString(),"\\;",";"));
				return clearWebMacros(script+postFix);
 			}
		}
		return "";
	}
}
