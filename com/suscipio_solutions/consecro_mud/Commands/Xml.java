package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


@SuppressWarnings("rawtypes")
public class Xml extends StdCommand
{
	public Xml(){}

	private final String[] access=I(new String[]{"XML"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		mob.tell(L("This command is deprecated, and no longer functions."));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return false;}


}
