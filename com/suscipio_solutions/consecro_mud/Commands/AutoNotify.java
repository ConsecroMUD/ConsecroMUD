package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;



@SuppressWarnings("rawtypes")
public class AutoNotify extends StdCommand
{

	public AutoNotify(){}

	private final String[] access=I(new String[]{"AUTONOTIFY"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.isAttribute(MOB.Attrib.AUTONOTIFY))
		{
			mob.setAttribute(MOB.Attrib.AUTONOTIFY,false);
			mob.tell(L("Notification of the arrival of your FRIENDS is now off."));
		}
		else
		{
			mob.setAttribute(MOB.Attrib.AUTONOTIFY,true);
			mob.tell(L("Notification of the arrival of your FRIENDS is now on."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
}
