package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;




@SuppressWarnings("rawtypes")
public class AutoDraw extends StdCommand
{
	public AutoDraw(){}

	private final String[] access=I(new String[]{"AUTODRAW"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(!mob.isAttribute(MOB.Attrib.AUTODRAW))
		{
			mob.setAttribute(MOB.Attrib.AUTODRAW,true);
			mob.tell(L("Auto weapon drawing has been turned on.  You will now draw a weapon when one is handy, and sheath one a few seconds after combat."));
		}
		else
		{
			mob.setAttribute(MOB.Attrib.AUTODRAW,false);
			mob.tell(L("Auto weapon drawing has been turned off.  You will no longer draw or sheath your weapon automatically."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}

