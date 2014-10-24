package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerAccount;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


@SuppressWarnings("rawtypes")
public class NoANSI extends StdCommand
{
	public NoANSI(){}

	private final String[] access=I(new String[]{"NOANSI","NOCOLOR","NOCOLOUR"});
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
			if(acct != null) acct.setFlag(PlayerAccount.FLAG_ANSI, false);
			if(mob.isAttribute(MOB.Attrib.ANSI))
			{
				mob.setAttribute(MOB.Attrib.ANSI,false);
				mob.tell(L("ANSI colour disabled.\n\r"));
			}
			else
			{
				mob.tell(L("ANSI is already disabled.\n\r"));
			}
			mob.session().setClientTelnetMode(Session.TELNET_ANSI,false);
			mob.session().setServerTelnetMode(Session.TELNET_ANSI,false);
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
