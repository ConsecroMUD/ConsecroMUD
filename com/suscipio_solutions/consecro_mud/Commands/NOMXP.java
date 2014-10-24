package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;


@SuppressWarnings("rawtypes")
public class NOMXP extends StdCommand
{
	public NOMXP(){}

	private final String[] access=I(new String[]{"NOMXP"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(!mob.isMonster())
		{
			if((mob.isAttribute(MOB.Attrib.MXP))
			||(mob.session().getClientTelnetMode(Session.TELNET_MXP)))
			{
				if(mob.session().getClientTelnetMode(Session.TELNET_MXP))
					mob.session().rawOut("\033[3z \033[7z");
				mob.setAttribute(MOB.Attrib.MXP,false);
				mob.session().changeTelnetMode(Session.TELNET_MXP,false);
				mob.session().setClientTelnetMode(Session.TELNET_MXP,false);
				mob.tell(L("MXP codes are disabled.\n\r"));
			}
			else
				mob.tell(L("MXP codes are already disabled.\n\r"));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return super.securityCheck(mob)&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.MXP));}
}
