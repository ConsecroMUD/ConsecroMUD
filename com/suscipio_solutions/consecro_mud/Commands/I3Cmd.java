package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Log;


@SuppressWarnings("rawtypes")
public class I3Cmd extends StdCommand
{
	public I3Cmd(){}

	private final String[] access=I(new String[]{"I3"});
	@Override public String[] getAccessWords(){return access;}

	public void i3Error(MOB mob)
	{
		if(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.I3))
			mob.tell(L("Try I3 LIST, I3 CHANNELS, I3 ADD [CHANNEL], I3 DELETE [CHANNEL], I3 LISTEN [CHANNEL], I3 SILENCE [CHANNEL], I3 PING [MUD], I3 LOCATE [NAME], I3 RESTART, or I3 INFO [MUD]."));
		else
			mob.tell(L("Try I3 LIST, I3 LOCATE [NAME], or I3 INFO [MUD-NAME]."));
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		commands.removeElementAt(0);
		if(commands.size()<1)
		{
			if(!CMLib.intermud().i3online())
			{
				mob.tell(L("I3 is unavailable."));
				return false;
			}
			i3Error(mob);
			return false;
		}
		final String str=(String)commands.firstElement();
		if((!CMLib.intermud().i3online())&&(!str.equalsIgnoreCase("restart")))
			mob.tell(L("I3 is unavailable."));
		else
		if(str.equalsIgnoreCase("list"))
			CMLib.intermud().giveI3MudList(mob);
		else
		if(str.equalsIgnoreCase("add"))
		{
			if(!CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.I3)){ i3Error(mob); return false;}
			if(commands.size()<2)
			{
				mob.tell(L("You did not specify a channel name!"));
				return false;
			}
			CMLib.intermud().i3channelAdd(mob,CMParms.combine(commands,1));
		}
		else
		if(str.equalsIgnoreCase("channels"))
			CMLib.intermud().giveI3ChannelsList(mob);
		else
		if(str.equalsIgnoreCase("delete"))
		{
			if(!CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.I3)){ i3Error(mob); return false;}
			if(commands.size()<2)
			{
				mob.tell(L("You did not specify a channel name!"));
				return false;
			}
			CMLib.intermud().i3channelRemove(mob,CMParms.combine(commands,1));
		}
		else
		if(str.equalsIgnoreCase("listen"))
		{
			if(!CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.I3)){ i3Error(mob); return false;}
			if(commands.size()<2)
			{
				mob.tell(L("You did not specify a channel name!"));
				return false;
			}
			CMLib.intermud().i3channelListen(mob,CMParms.combine(commands,1));
		}
		else
		if(str.equalsIgnoreCase("ping"))
		{
			if(!CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.I3)){ i3Error(mob); return false;}
			CMLib.intermud().i3pingRouter(mob);
		}
		else
		if(str.equalsIgnoreCase("restart"))
		{
			if(!CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.I3)){ i3Error(mob); return false;}
			try
			{
				mob.tell(CMLib.hosts().get(0).executeCommand("START I3"));
			}catch(final Exception e){ Log.errOut("I3Cmd",e);}
		}
		else
		if(str.equalsIgnoreCase("locate"))
		{
			if(commands.size()<2)
			{
				mob.tell(L("You did not specify a name!"));
				return false;
			}
			CMLib.intermud().i3locate(mob,CMParms.combine(commands,1));
		}
		else
		if(str.equalsIgnoreCase("silence"))
		{
			if(!CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.I3)){ i3Error(mob); return false;}
			if(commands.size()<2)
			{
				mob.tell(L("You did not specify a channel name!"));
				return false;
			}
			CMLib.intermud().i3channelSilence(mob,CMParms.combine(commands,1));
		}
		else
		if(str.equalsIgnoreCase("info"))
			CMLib.intermud().i3mudInfo(mob,CMParms.combine(commands,1));
		else
			i3Error(mob);

		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
