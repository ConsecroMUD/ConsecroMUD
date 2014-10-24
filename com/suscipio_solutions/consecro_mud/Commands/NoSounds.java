package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;


@SuppressWarnings("rawtypes")
public class NoSounds extends StdCommand
{
	public NoSounds(){}

	private final String[] access=I(new String[]{"NOSOUNDS","NOMSP"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(!mob.isMonster())
		{
			if((mob.isAttribute(MOB.Attrib.SOUND))
			||(mob.session().getClientTelnetMode(Session.TELNET_MSP)))
			{
				mob.setAttribute(MOB.Attrib.SOUND,false);
				mob.session().changeTelnetMode(Session.TELNET_MSP,false);
				mob.session().setClientTelnetMode(Session.TELNET_MSP,false);
				mob.tell(L("MSP Sound/Music disabled.\n\r"));
			}
			else
				mob.tell(L("MSP Sound/Music already disabled.\n\r"));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return super.securityCheck(mob)&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.MSP));}
}
