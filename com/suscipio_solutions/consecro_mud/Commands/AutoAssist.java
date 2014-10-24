package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;



@SuppressWarnings("rawtypes")
public class AutoAssist extends StdCommand
{
	public AutoAssist(){}

	private final String[] access=I(new String[]{"AUTOASSIST"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.isAttribute(MOB.Attrib.AUTOASSIST))
		{
			mob.setAttribute(MOB.Attrib.AUTOASSIST,false);
			mob.tell(L("Autoassist has been turned on."));
		}
		else
		{
			mob.setAttribute(MOB.Attrib.AUTOASSIST,true);
			mob.tell(L("Autoassist has been turned off."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}

