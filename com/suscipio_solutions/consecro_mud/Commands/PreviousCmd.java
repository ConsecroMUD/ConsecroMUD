package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;


@SuppressWarnings({"unchecked","rawtypes"})
public class PreviousCmd extends StdCommand
{
	public PreviousCmd(){}

	private final String[] access=I(new String[]{"!"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(!mob.isMonster())
		{
			mob.enqueCommand((List)CMParms.copyFlattenVector(mob.session().getPreviousCMD()),metaFlags,0);
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
