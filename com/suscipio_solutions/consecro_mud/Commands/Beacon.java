package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;


@SuppressWarnings("rawtypes")
public class Beacon extends StdCommand
{
	public Beacon(){}

	private final String[] access=I(new String[]{"BEACON"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		commands.removeElementAt(0);
		if(commands.size()==0)
		{
			if(mob.getStartRoom()==mob.location())
				mob.tell(L("This is already your beacon."));
			else
			{
				mob.setStartRoom(mob.location());
				mob.tell(L("You have modified your beacon."));
			}
		}
		else
		{
			final String name=CMParms.combine(commands,0);
			MOB M=CMLib.sessions().findPlayerOnline(name,true);
			if(M==null) M=CMLib.sessions().findPlayerOnline(name,false);
			if(M==null)
			{
				mob.tell(L("No one is online called '@x1'!",name));
				return false;
			}
			if(M.getStartRoom()==M.location())
			{
				mob.tell(L("@x1 is already at their beacon.",M.name(mob)));
				return false;
			}
			if(!CMSecurity.isAllowed(mob,M.location(),CMSecurity.SecFlag.BEACON))
			{
				mob.tell(L("You cannot beacon @x1 there.",M.name(mob)));
				return false;
			}
			M.setStartRoom(M.location());
			mob.tell(L("You have modified @x1's beacon.",M.name(mob)));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.BEACON);}


}
