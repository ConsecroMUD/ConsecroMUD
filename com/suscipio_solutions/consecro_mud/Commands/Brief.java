package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


@SuppressWarnings("rawtypes")
public class Brief extends StdCommand
{
	public Brief(){}

	private final String[] access=I(new String[]{"BRIEF"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.isAttribute(MOB.Attrib.BRIEF))
		{
			mob.setAttribute(MOB.Attrib.BRIEF,false);
			mob.tell(L("Brief room descriptions are now off."));
		}
		else
		{
			mob.setAttribute(MOB.Attrib.BRIEF,true);
			mob.tell(L("Brief room descriptions are now on."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
