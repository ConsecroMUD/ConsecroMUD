package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;


@SuppressWarnings("rawtypes")
public class At extends StdCommand
{
	public At(){}

	private final String[] access=I(new String[]{"AT"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		commands.removeElementAt(0);
		if(commands.size()==0)
		{
			mob.tell(L("At where do what?"));
			return false;
		}
		final String cmd=(String)commands.firstElement();
		commands.removeElementAt(0);
		final Room room=CMLib.map().findWorldRoomLiberally(mob,cmd,"APMIR",100,120000);
		if(room==null)
		{
			if(CMSecurity.isAllowedAnywhere(mob,CMSecurity.SecFlag.AT))
				mob.tell(L("At where? Try a Room ID, player name, area name, or room text!"));
			else
				mob.tell(L("You aren't powerful enough to do that."));
			return false;
		}
		if(!CMSecurity.isAllowed(mob,room,CMSecurity.SecFlag.AT))
		{
			mob.tell(L("You aren't powerful enough to do that there."));
			return false;
		}
		final Room R=mob.location();
		if(R!=room)	room.bringMobHere(mob,false);
		mob.doCommand(commands,metaFlags);
		if(mob.location()!=R) R.bringMobHere(mob,false);
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowedAnywhere(mob,CMSecurity.SecFlag.AT);}


}
