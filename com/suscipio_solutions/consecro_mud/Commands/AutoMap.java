package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMProps;



@SuppressWarnings("rawtypes")
public class AutoMap extends StdCommand
{
	public AutoMap(){}

	private final String[] access=I(new String[]{"AUTOMAP"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(!mob.isAttribute(MOB.Attrib.AUTOMAP))
		{
			mob.setAttribute(MOB.Attrib.AUTOMAP,true);
			mob.tell(L("Automap has been turned off."));
		}
		else
		{
			mob.setAttribute(MOB.Attrib.AUTOMAP,false);
			mob.tell(L("Automap has been turned on."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMProps.getIntVar(CMProps.Int.AWARERANGE)>0;}


}

