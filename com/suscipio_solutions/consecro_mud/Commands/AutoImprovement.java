package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;




@SuppressWarnings("rawtypes")
public class AutoImprovement extends StdCommand
{
	public AutoImprovement(){}

	private final String[] access=I(new String[]{"AUTOIMPROVEMENT"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.isAttribute(MOB.Attrib.AUTOIMPROVE))
		{
			mob.setAttribute(MOB.Attrib.AUTOIMPROVE,false);
			mob.tell(L("Skill improvement notifications are now off."));
		}
		else
		{
			mob.setAttribute(MOB.Attrib.AUTOIMPROVE,true);
			mob.tell(L("Skill improvement notifications are now on."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}

