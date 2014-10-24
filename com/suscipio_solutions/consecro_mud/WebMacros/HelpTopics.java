package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.List;

import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class HelpTopics extends StdWebMacro
{
	@Override public String name() { return "HelpTopics"; }

	@Override @SuppressWarnings({ "unchecked", "rawtypes" })
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("HELPTOPIC");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("HELPTOPIC");
			httpReq.removeUrlParameter("HELPFIRSTLETTER");
			return "";
		}
		else
		if(parms.containsKey("DATA"))
		{
			int limit=78;
			if(parms.containsKey("LIMIT")) limit=CMath.s_int(parms.get("LIMIT"));
			if((last!=null)&&(last.length()>0))
			{
				final StringBuilder s=CMLib.help().getHelpText(last,null,parms.containsKey("IMMHELP"));
				if(s!=null)
					return clearWebMacros(helpHelp(s,limit).toString());
			}
			return "";
		}
		else
		if(parms.containsKey("NEXTLETTER"))
		{
			String fletter=httpReq.getUrlParameter("HELPFIRSTLETTER");
			if((fletter==null)||(fletter.length()==0))
				fletter="A";
			else
			if(fletter.charAt(0)>='Z')
			{
				httpReq.addFakeUrlParameter("HELPFIRSTLETTER","");
				return " @break@";
			}
			else
				fletter=Character.toString((char)(fletter.charAt(0)+1));
			httpReq.addFakeUrlParameter("HELPFIRSTLETTER",fletter);
		}
		else
		if(parms.containsKey("NEXT"))
		{
			List<String> topics=null;
			if(parms.containsKey("IMMORTAL"))
			{
				topics=(List)httpReq.getRequestObjects().get("HELP_IMMORTALTOPICS");
				if(topics==null)
				{
					topics=CMLib.help().getTopics(true,false);
					httpReq.getRequestObjects().put("HELP_IMMORTALTOPICS", topics);
				}
			}
			else
			if(parms.containsKey("BOTH"))
			{
				topics=(List)httpReq.getRequestObjects().get("HELP_BOTHTOPICS");
				if(topics==null)
				{
					topics=CMLib.help().getTopics(true,true);
					httpReq.getRequestObjects().put("HELP_BOTHTOPICS", topics);
				}
			}
			else
			{
				topics=(List)httpReq.getRequestObjects().get("HELP_HELPTOPICS");
				if(topics==null)
				{
					topics=CMLib.help().getTopics(false,true);
					httpReq.getRequestObjects().put("HELP_HELPTOPICS", topics);
				}
			}

			final boolean noables=parms.containsKey("SHORT");
			String fletter=parms.get("FIRSTLETTER");
			if(fletter==null) fletter=httpReq.getUrlParameter("FIRSTLETTER");
			if(fletter==null) fletter="";

			String lastID="";
			for(int h=0;h<topics.size();h++)
			{
				final String topic=topics.get(h);
				if(noables&&CMLib.help().isPlayerSkill(topic))
				   continue;
				if(topic.startsWith(fletter)||(fletter.length()==0))
				if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!topic.equals(lastID))))
				{
					httpReq.addFakeUrlParameter("HELPTOPIC",topic);
					return "";
				}
				lastID=topic;
			}
			httpReq.addFakeUrlParameter("HELPTOPIC","");
			if(parms.containsKey("EMPTYOK"))
				return "<!--EMPTY-->";
			return " @break@";
		}
		else
		if(last!=null)
			return last;
		return "<!--EMPTY-->";
	}

}
