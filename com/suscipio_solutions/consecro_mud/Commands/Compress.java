package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


@SuppressWarnings("rawtypes")
public class Compress extends StdCommand
{
	public Compress(){}

	private final String[] access=I(new String[]{"COMPRESS"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.isAttribute(MOB.Attrib.COMPRESS))
		{
			mob.setAttribute(MOB.Attrib.COMPRESS,false);
			mob.tell(L("Compressed views are now inactive."));
		}
		else
		{
			mob.setAttribute(MOB.Attrib.COMPRESS,true);
			mob.tell(L("Compressed views are now active."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
