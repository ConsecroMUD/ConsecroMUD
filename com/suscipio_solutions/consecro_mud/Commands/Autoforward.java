package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMProps;




@SuppressWarnings("rawtypes")
public class Autoforward extends StdCommand
{
	public Autoforward(){}

	private final String[] access=I(new String[]{"AUTOFORWARD"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(!CMProps.getBoolVar(CMProps.Bool.EMAILFORWARDING))
		{
			mob.tell(L("This feature is not activated."));
			return false;
		}
		if(mob.isAttribute(MOB.Attrib.AUTOFORWARD))
		{
			mob.setAttribute(MOB.Attrib.AUTOFORWARD,false);
			mob.tell(L("Autoemail forwarding has been turned on."));
		}
		else
		{
			mob.setAttribute(MOB.Attrib.AUTOFORWARD,true);
			mob.tell(L("Autoemail forwarding has been turned off."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}

