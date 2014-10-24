package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Properties;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;

@SuppressWarnings("rawtypes")
public class Topics extends IMMTopics
{
	public Topics(){}

	private final String[] access=I(new String[]{"TOPICS"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final Properties helpFile=CMLib.help().getHelpFile();
		if(helpFile.size()==0)
		{
			if(mob!=null)
				mob.tell(L("No help is available."));
			return false;
		}

		doTopics(mob,helpFile,"HELP", "PLAYER TOPICS");
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


	@Override public boolean securityCheck(MOB mob){return true;}
}
