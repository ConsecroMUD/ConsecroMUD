package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Resources;

@SuppressWarnings({"unchecked","rawtypes"})
public class IMMTopics extends StdCommand
{
	public IMMTopics(){}

	private final String[] access=I(new String[]{"IMMTOPICS"});
	@Override public String[] getAccessWords(){return access;}

	public static void doTopics(MOB mob, Properties rHelpFile, String helpName, String resName)
	{
		StringBuffer topicBuffer=(StringBuffer)Resources.getResource(resName);
		if(topicBuffer==null)
		{
			topicBuffer=new StringBuffer();

			final Vector reverseList=new Vector();
			for(final Enumeration e=rHelpFile.keys();e.hasMoreElements();)
			{
				final String ptop = (String)e.nextElement();
				final String thisTag=rHelpFile.getProperty(ptop);
				if ((thisTag==null)||(thisTag.length()==0)||(thisTag.length()>=35)
					|| (rHelpFile.getProperty(thisTag)== null) )
						reverseList.addElement(ptop);
			}

			Collections.sort(reverseList);
			topicBuffer=new StringBuffer("Help topics: \n\r\n\r");
			topicBuffer.append(CMLib.lister().fourColumns(mob,reverseList,"HELP"));
			topicBuffer=new StringBuffer(topicBuffer.toString().replace('_',' '));
			Resources.submitResource(resName,topicBuffer);
		}
		if((mob!=null)&&(!mob.isMonster()))
			mob.session().colorOnlyPrintln(CMLib.lang().L("@x1\n\r\n\rEnter @x2 (TOPIC NAME) for more information.",topicBuffer.toString(),helpName),false);
	}


	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final Properties immHelpFile=CMLib.help().getImmHelpFile();
		if(immHelpFile.size()==0)
		{
			if(mob!=null)
				mob.tell(L("No immortal help is available."));
			return false;
		}

		doTopics(mob,immHelpFile,"IMMHELP", "IMMORTAL TOPICS");
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.IMMHELP);}


}
