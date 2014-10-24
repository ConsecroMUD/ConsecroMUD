package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;


@SuppressWarnings("rawtypes")
public class Sounds extends StdCommand
{
	public Sounds(){}

	private final String[] access=I(new String[]{"SOUNDS","MSP"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(!mob.isMonster())
		{
			boolean force=false;
			if(commands != null)
				for(final Object o : commands)
					if(o.toString().equalsIgnoreCase("force"))
						force=true;
			final Session session=mob.session();
			if((!mob.isAttribute(MOB.Attrib.SOUND))
			||(!session.getClientTelnetMode(Session.TELNET_MSP)))
			{
				session.changeTelnetMode(Session.TELNET_MSP,true);
				for(int i=0;((i<5)&&(!session.getClientTelnetMode(Session.TELNET_MSP)));i++)
				{
					try{mob.session().prompt("",500);}catch(final Exception e){}
				}
				if(session.getClientTelnetMode(Session.TELNET_MSP))
				{
					mob.setAttribute(MOB.Attrib.SOUND,true);
					mob.tell(L("MSP Sound/Music enabled.\n\r"));
				}
				else
				if(force)
				{
					session.setClientTelnetMode(Session.TELNET_MSP, true);
					session.setServerTelnetMode(Session.TELNET_MSP, true);
					mob.setAttribute(MOB.Attrib.SOUND,true);
					mob.tell(L("MSP Sound/Music has been forceably enabled.\n\r"));
				}
				else
					mob.tell(L("Your client does not appear to support MSP."));
			}
			else
			{
				mob.tell(L("MSP Sound/Music is already enabled.\n\r"));
			}
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return super.securityCheck(mob)&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.MSP));}
}
