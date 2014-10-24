package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


@SuppressWarnings("rawtypes")
public class NoTeach extends StdCommand
{
	public NoTeach(){}

	private final String[] access=I(new String[]{"NOTEACH"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.isAttribute(MOB.Attrib.NOTEACH))
		{
			mob.setAttribute(MOB.Attrib.NOTEACH,false);
			mob.tell(L("You may now teach, train, or learn."));
		}
		else
		{
			mob.setAttribute(MOB.Attrib.NOTEACH,true);
			mob.tell(L("You are no longer teaching, training, or learning."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
