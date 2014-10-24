package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;




@SuppressWarnings("rawtypes")
public class AutoWeather extends StdCommand
{
	public AutoWeather(){}

	private final String[] access=I(new String[]{"AUTOWEATHER"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException

	{
		if(mob.isAttribute(MOB.Attrib.AUTOWEATHER))
		{
			mob.setAttribute(MOB.Attrib.AUTOWEATHER,false);
			mob.tell(L("Weather descriptions are now off."));
		}
		else
		{
			mob.setAttribute(MOB.Attrib.AUTOWEATHER,true);
			mob.tell(L("Weather descriptions are now on."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
}

