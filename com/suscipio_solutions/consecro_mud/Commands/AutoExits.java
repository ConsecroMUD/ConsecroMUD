package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;




@SuppressWarnings("rawtypes")
public class AutoExits extends StdCommand
{
	public AutoExits(){}

	private final String[] access=I(new String[]{"AUTOEXITS"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.isAttribute(MOB.Attrib.AUTOEXITS))
		{
			mob.setAttribute(MOB.Attrib.AUTOEXITS,false);
			mob.tell(L("Autoexits has been turned off."));
		}
		else
		{
			mob.setAttribute(MOB.Attrib.AUTOEXITS,true);
			mob.tell(L("Autoexits has been turned on."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}

