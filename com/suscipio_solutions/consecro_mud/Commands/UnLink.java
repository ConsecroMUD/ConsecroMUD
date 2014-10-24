package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;


@SuppressWarnings({"unchecked","rawtypes"})
public class UnLink extends StdCommand
{
	public UnLink(){}

	private final String[] access=I(new String[]{"UNLINK"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		commands.setElementAt("DESTROY",0);
		commands.insertElementAt("ROOM",1);
		final Command C=CMClass.getCommand("Destroy");
		C.execute(mob,commands,metaFlags);
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CMDEXITS);}


}
