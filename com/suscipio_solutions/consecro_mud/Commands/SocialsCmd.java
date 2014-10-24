package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;


@SuppressWarnings("rawtypes")
public class SocialsCmd extends StdCommand
{
	public SocialsCmd(){}

	private final String[] access=I(new String[]{"SOCIALS"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(!mob.isMonster())
			mob.session().colorOnlyPrintln(L("^HComplete socials list:^?\n\r@x1",CMLib.socials().getSocialsTable()));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
