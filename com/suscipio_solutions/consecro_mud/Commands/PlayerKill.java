package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMProps;


@SuppressWarnings("rawtypes")
public class PlayerKill extends StdCommand
{
	public PlayerKill(){}

	private final String[] access=I(new String[]{"PLAYERKILL","PKILL","PVP"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(CMProps.getVar(CMProps.Str.PKILL).startsWith("ALWAYS")
			||CMProps.getVar(CMProps.Str.PKILL).startsWith("NEVER"))
		{
			mob.tell(L("This option has been disabled."));
			return false;
		}

		if(mob.isInCombat())
		{
			mob.tell(L("YOU CANNOT TOGGLE THIS FLAG WHILE IN COMBAT!"));
			return false;
		}
		if(mob.isAttribute(MOB.Attrib.PLAYERKILL))
		{
			if(CMProps.getVar(CMProps.Str.PKILL).startsWith("ONEWAY"))
			{
				mob.tell(L("Once turned on, this flag may not be turned off again."));
				return false;
			}

			if((mob.session()!=null)
			&&(mob.session().getLastPKFight()>0)
			&&((System.currentTimeMillis()-mob.session().getLastPKFight())<(5*60*1000)))
			{
				mob.tell(L("You'll need to wait a few minutes before you can turn off your PK flag."));
				return false;
			}

			mob.setAttribute(MOB.Attrib.PLAYERKILL,false);
			mob.tell(L("Your playerkill flag has been turned off."));
		}
		else
		if(!mob.isMonster())
		{
			mob.tell(L("Turning on this flag will allow you to kill and be killed by other players."));
			if(CMProps.getVar(CMProps.Str.PKILL).startsWith("ONEWAY"))
				mob.tell(L("Once turned on, this flag may not be turned off again."));
			if(mob.session().confirm(L("Are you absolutely sure (y/N)?"),L("N")))
			{
				mob.setAttribute(MOB.Attrib.PLAYERKILL,true);
				mob.tell(L("Your playerkill flag has been turned on."));
			}
			else
				mob.tell(L("Your playerkill flag remains OFF."));
			if(!CMProps.getVar(CMProps.Str.PKILL).startsWith("ONEWAY"))
				mob.tell(L("Both players must have their playerkill flag turned on for sparring."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return false;}


}
