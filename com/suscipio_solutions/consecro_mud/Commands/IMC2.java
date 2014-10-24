package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Log;


@SuppressWarnings("rawtypes")
public class IMC2 extends StdCommand
{
	public IMC2(){}

	private final String[] access=I(new String[]{"IMC2"});
	@Override public String[] getAccessWords(){return access;}

	public void IMC2Error(MOB mob)
	{
		if(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.IMC2))
			mob.tell(L("Try IMC2 LIST, IMC2 INFO [MUD], IMC2 LOCATE, IMC2 RESTART, or IMC2 CHANNELS."));
		else
			mob.tell(L("Try IMC2 LIST, IMC2 INFO [MUD], IMC2 LOCATE"));
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(!(CMLib.intermud().imc2online()))
		{
			mob.tell(L("IMC2 is unavailable."));
			return false;
		}
		commands.removeElementAt(0);
		if(commands.size()<1)
		{
			IMC2Error(mob);
			return false;
		}
		final String str=(String)commands.firstElement();
		if(!(CMLib.intermud().imc2online()))
			mob.tell(L("IMC2 is unavailable."));
		else
		if(str.equalsIgnoreCase("list"))
			CMLib.intermud().giveIMC2MudList(mob);
		else
		if(str.equalsIgnoreCase("locate"))
			CMLib.intermud().i3locate(mob,CMParms.combine(commands,1));
		else
		if(str.equalsIgnoreCase("channels") && CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.IMC2))
			CMLib.intermud().giveIMC2ChannelsList(mob);
		else
		if(str.equalsIgnoreCase("info"))
			CMLib.intermud().imc2mudInfo(mob,CMParms.combine(commands,1));
		else
		if(str.equalsIgnoreCase("restart") && CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.IMC2))
		{
			try
			{
				mob.tell(CMLib.hosts().get(0).executeCommand("START IMC2"));
			}catch(final Exception e){ Log.errOut("IMC2Cmd",e);}
		}
		else
			IMC2Error(mob);

		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
