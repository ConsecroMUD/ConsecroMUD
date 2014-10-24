package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;


@SuppressWarnings("rawtypes")
public class SysMsgs extends StdCommand
{
	public SysMsgs(){}

	private final String[] access=I(new String[]{"SYSMSGS"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.isAttribute(MOB.Attrib.SYSOPMSGS))
			mob.setAttribute(MOB.Attrib.SYSOPMSGS,false);
		else
			mob.setAttribute(MOB.Attrib.SYSOPMSGS,true);
		mob.tell(L("Extended messages are now : @x1",((mob.isAttribute(MOB.Attrib.SYSOPMSGS))?"ON":"OFF")));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.SYSMSGS);}


}
