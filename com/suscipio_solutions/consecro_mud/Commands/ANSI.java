package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerAccount;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


@SuppressWarnings("rawtypes")
public class ANSI extends StdCommand
{
	public ANSI(){}

	private final String[] access=I(new String[]{"ANSI","COLOR","COLOUR"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(!mob.isMonster())
		{
			PlayerAccount acct = null;
			if(mob.playerStats()!=null)
				acct = mob.playerStats().getAccount();
			if(acct != null) acct.setFlag(PlayerAccount.FLAG_ANSI, true);
			if(!mob.isAttribute(MOB.Attrib.ANSI))
			{
				mob.setAttribute(MOB.Attrib.ANSI,true);
				mob.tell(L("^!ANSI^N ^Hcolour^N enabled.\n\r"));
			}
			else
			{
				mob.tell(L("^!ANSI^N is ^Halready^N enabled.\n\r"));
			}
			mob.session().setClientTelnetMode(Session.TELNET_ANSI,true);
			mob.session().setServerTelnetMode(Session.TELNET_ANSI,true);
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
