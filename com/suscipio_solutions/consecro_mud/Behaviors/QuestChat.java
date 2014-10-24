package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Quest;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;

@SuppressWarnings({"unchecked","rawtypes"})
public class QuestChat extends MudChat
{

	@Override public String ID(){return "QuestChat";}
	private final Map<String,List<String>> alreadySaid=new Hashtable<String,List<String>>();
	private String myQuestName=null;

	@Override public void registerDefaultQuest(String questName){ myQuestName=questName;}


	@Override
	protected boolean match(MOB speaker, String expression, String message, String[] rest)
	{
		if(expression.indexOf("::")>=0)
		{
			 int x=expression.length()-1;
			 char c=' ';
			 boolean coded=false;
			 while(x>=0)
			 {
				 c=expression.charAt(x);
				 if((c==':')&&(x>0)&&(expression.charAt(x-1)==':'))
				 {
					 if(coded)
					 {
						 final String codeStr=expression.substring(x+2).toUpperCase().trim();
						 expression=expression.substring(0,x-1).trim();
						 List<String> V=alreadySaid.get(speaker.Name().toUpperCase());
						 if(V==null)
						 {
							 V=new Vector();
							 alreadySaid.put(speaker.Name().toUpperCase(),V);
						 }
						 else
						 if(V.contains(codeStr))
							 return false;
						 if(super.match(speaker,expression,message,rest))
						 {
							 V.add(codeStr);
							 if((myQuestName!=null)&&(myQuestName.length()>0))
							 {
								 final Quest myQuest=CMLib.quests().fetchQuest(myQuestName);
								 if(myQuest!=null)
								 {
									 String stat=myQuest.getStat("CHAT:"+speaker.Name().toUpperCase());
									 if(stat.length()>0) stat+=" ";
									 myQuest.setStat("CHAT:"+speaker.Name().toUpperCase(),stat+codeStr);
								 }
							 }
							 return true;
						 }
						 return false;
					 }
					 break;
				 }
				 coded=true;
				 x--;
			 }
		}
		return super.match(speaker,expression,message,rest);
	}
}
