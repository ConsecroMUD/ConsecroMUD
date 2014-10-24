package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;


@SuppressWarnings("rawtypes")
public class AFK extends StdCommand
{
	public AFK(){}

	private final String[] access=I(new String[]{"AFK"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.session()==null) return false;
		if(mob.session().isAfk())
			mob.session().setAfkFlag(false);
		else
		{
			mob.session().setAFKMessage(CMParms.combine(commands,1));
			mob.session().setAfkFlag(true);
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
