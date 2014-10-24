package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;


@SuppressWarnings("rawtypes")
public class Ban extends StdCommand
{
	public Ban(){}

	private final String[] access=I(new String[]{"BAN"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		commands.removeElementAt(0);
		String banMe=CMParms.combine(commands,0);
		if(banMe.length()==0)
		{
			mob.tell(L("Ban what?  Enter an IP address or name mask."));
			return false;
		}
		banMe=banMe.toUpperCase().trim();
		final int b=CMSecurity.ban(banMe);
		if(b<0)
			mob.tell(L("Logins and IPs matching @x1 are now banned.",banMe));
		else
		{
			mob.tell(L("That is already banned.  Do LIST BANNED and check out #@x1.",""+(b+1)));
			return false;
		}
		return true;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.BAN);}


}
