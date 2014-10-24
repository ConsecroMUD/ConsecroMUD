package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;



@SuppressWarnings("rawtypes")
public class AutoRun extends StdCommand
{
	public AutoRun(){}

	private final String[] access=I(new String[]{"AUTORUN"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.isAttribute(MOB.Attrib.AUTORUN))
		{
			mob.setAttribute(MOB.Attrib.AUTORUN,false);
			mob.tell(L("Auto-Run has been turned off."));
		}
		else
		{
			mob.setAttribute(MOB.Attrib.AUTORUN,true);
			mob.tell(L("Auto-Run has been turned on."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}

