package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


@SuppressWarnings("rawtypes")
public class Quiet extends StdCommand
{
	public Quiet(){}

	private final String[] access=I(new String[]{"QUIET"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(!mob.isAttribute(MOB.Attrib.QUIET))
		{
			mob.setAttribute(MOB.Attrib.QUIET,true);
			mob.tell(L("Quiet mode is now on.  You will no longer receive channel messages or tells."));
		}
		else
		{
			mob.setAttribute(MOB.Attrib.QUIET,false);
			mob.tell(L("Quiet mode is now off.  You may now receive channel messages and tells."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
