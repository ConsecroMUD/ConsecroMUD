package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings({"unchecked","rawtypes"})
public class Tell extends StdCommand
{
	public Tell(){}

	private final String[] access=I(new String[]{"TELL","T"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if((!mob.isMonster())&&mob.isAttribute(MOB.Attrib.QUIET))
		{
			mob.tell(L("You have QUIET mode on.  You must turn it off first."));
			return false;
		}

		if(commands.size()<3)
		{
			mob.tell(L("Tell whom what?"));
			return false;
		}
		commands.removeElementAt(0);

		if(((String)commands.firstElement()).equalsIgnoreCase("last")
		   &&(CMath.isNumber(CMParms.combine(commands,1)))
		   &&(mob.playerStats()!=null))
		{
			final java.util.List<String> V=mob.playerStats().getTellStack();
			if((V.size()==0)
			||(CMath.bset(metaFlags,Command.METAFLAG_AS))
			||(CMath.bset(metaFlags,Command.METAFLAG_POSSESSED)))
				mob.tell(L("No telling."));
			else
			{
				int num=CMath.s_int(CMParms.combine(commands,1));
				if(num>V.size()) num=V.size();
				final Session S=mob.session();
				try
				{
					if(S!=null) S.snoopSuspension(1);
					for(int i=V.size()-num;i<V.size();i++)
						mob.tell(V.get(i));
				}
				finally
				{
					if(S!=null) S.snoopSuspension(-1);
				}
			}
			return false;
		}

		MOB targetM=null;
		String targetName=((String)commands.elementAt(0)).toUpperCase();
		targetM=CMLib.sessions().findPlayerOnline(targetName,true);
		if(targetM==null) targetM=CMLib.sessions().findPlayerOnline(targetName,false);
		for(int i=1;i<commands.size();i++)
		{
			final String s=(String)commands.elementAt(i);
			if(s.indexOf(' ')>=0)
				commands.setElementAt("\""+s+"\"",i);
		}
		String combinedCommands=CMParms.combine(commands,1);
		if(combinedCommands.equals(""))
		{
			mob.tell(L("Tell them what?"));
			return false;
		}
		combinedCommands=CMProps.applyINIFilter(combinedCommands,CMProps.Str.SAYFILTER);
		if(targetM==null)
		{
			if(targetName.indexOf('@')>=0)
			{
				final String mudName=targetName.substring(targetName.indexOf('@')+1);
				targetName=targetName.substring(0,targetName.indexOf('@'));
				if(CMLib.intermud().i3online()||CMLib.intermud().imc2online())
					CMLib.intermud().i3tell(mob,targetName,mudName,combinedCommands);
				else
					mob.tell(L("Intermud is unavailable."));
				return false;
			}
			mob.tell(L("That person doesn't appear to be online."));
			return false;
		}

		if(targetM.isAttribute(MOB.Attrib.QUIET))
		{
			mob.tell(L("That person can not hear you."));
			return false;
		}


		final Session ts=targetM.session();
		try
		{
			if(ts!=null) ts.snoopSuspension(1);
			CMLib.commands().postSay(mob,targetM,combinedCommands,true,true);
		}
		finally
		{
			if(ts!=null) ts.snoopSuspension(-1);
		}

		if((targetM.session()!=null)&&(targetM.session().isAfk()))
		{
			mob.tell(targetM.session().getAfkMessage());
		}
		return false;
	}
	// the reason this is not 0ed is because of combat -- we want the players to use SAY, and pay for it when coordinating.
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public boolean canBeOrdered(){return false;}


}
