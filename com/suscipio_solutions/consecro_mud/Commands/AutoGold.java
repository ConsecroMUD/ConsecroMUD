package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;



@SuppressWarnings("rawtypes")
public class AutoGold extends StdCommand
{
	public AutoGold(){}

	private final String[] access=I(new String[]{"AUTOGOLD"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.isAttribute(MOB.Attrib.AUTOGOLD))
		{
			mob.setAttribute(MOB.Attrib.AUTOGOLD,false);
			mob.tell(L("Autogold has been turned off."));
		}
		else
		{
			mob.setAttribute(MOB.Attrib.AUTOGOLD,true);
			mob.tell(L("Autogold has been turned on."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}

