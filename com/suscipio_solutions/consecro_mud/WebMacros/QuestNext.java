package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Quest;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings({"unchecked","rawtypes"})
public class QuestNext extends StdWebMacro
{
	@Override public String name() { return "QuestNext"; }
	@Override public boolean isAdminMacro()	{return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		String last=httpReq.getUrlParameter("QUEST");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("QUEST");
			return "";
		}
		if(last!=null) last=CMStrings.replaceAll(last,"*","@");
		String lastID="";

		final Vector V=new Vector();
		for(int q=0;q<CMLib.quests().numQuests();q++)
			V.addElement(CMLib.quests().fetchQuest(q));
		final Vector sortedV=new Vector();
		while(V.size()>0)
		{
			Quest lowQ=(Quest)V.firstElement();
			for(int v=1;v<V.size();v++)
				if(((Quest)V.elementAt(v)).name().toUpperCase().compareTo(lowQ.name().toUpperCase())<0)
					lowQ=(Quest)V.elementAt(v);
			V.remove(lowQ);
			sortedV.addElement(lowQ);
		}

		for(int q=0;q<sortedV.size();q++)
		{
			final Quest Q=(Quest)sortedV.elementAt(q);
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!(""+Q).equals(lastID))))
			{
				httpReq.addFakeUrlParameter("QUEST",CMStrings.replaceAll(""+Q,"@","*"));
				return "";
			}
			lastID=""+Q;
		}
		httpReq.addFakeUrlParameter("QUEST","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
