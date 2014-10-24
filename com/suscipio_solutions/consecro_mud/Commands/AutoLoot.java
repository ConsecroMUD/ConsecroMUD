package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;



@SuppressWarnings("rawtypes")
public class AutoLoot extends StdCommand
{
	public AutoLoot(){}

	private final String[] access=I(new String[]{"AUTOLOOT"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.isAttribute(MOB.Attrib.AUTOLOOT))
		{
			mob.setAttribute(MOB.Attrib.AUTOLOOT,false);
			mob.tell(L("Autolooting has been turned off."));
		}
		else
		{
			mob.setAttribute(MOB.Attrib.AUTOLOOT,true);
			mob.tell(L("Autolooting has been turned on."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
}

