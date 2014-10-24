package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMStrings;


@SuppressWarnings("rawtypes")
public class Ignore extends StdCommand
{
	public Ignore(){}

	private final String[] access=I(new String[]{"IGNORE"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final PlayerStats pstats=mob.playerStats();
		if(pstats==null) return false;
		final Set<String> h=pstats.getIgnored();
		if((commands.size()<2)||(((String)commands.elementAt(1)).equalsIgnoreCase("list")))
		{
			if(h.size()==0)
				mob.tell(L("You have no names on your ignore list.  Use IGNORE ADD to add more."));
			else
			{
				final StringBuffer str=new StringBuffer(L("You are ignoring: "));
				for (final Object element : h)
					str.append(((String)element)+" ");
				mob.tell(str.toString());
			}
		}
		else
		if(((String)commands.elementAt(1)).equalsIgnoreCase("ADD"))
		{
			String name=CMParms.combine(commands,2);
			if(name.length()==0)
			{
				mob.tell(L("Add whom?"));
				return false;
			}
			name=CMStrings.capitalizeAndLower(name);
			if((!CMLib.players().playerExists(name))&&(name.indexOf('@')<0))
			{
				mob.tell(L("No player by that name was found."));
				return false;
			}
			if(h.contains(name))
			{
				mob.tell(L("That name is already on your list."));
				return false;
			}
			h.add(name);
			mob.tell(L("The Player '@x1' has been added to your ignore list.",name));
		}
		else
		if(((String)commands.elementAt(1)).equalsIgnoreCase("REMOVE"))
		{
			final String name=CMParms.combine(commands,2);
			if(name.length()==0)
			{
				mob.tell(L("Remove whom?"));
				return false;
			}
			if(!h.contains(name))
			{
				mob.tell(L("That name '@x1' does not appear on your list.  Watch your casing!",name));
				return false;
			}
			h.remove(name);
			mob.tell(L("The Player '@x1' has been removed from your ignore list.",name));
		}
		else
		{
			mob.tell(L("Parameter '@x1' is not recognized.  Try LIST, ADD, or REMOVE.",((String)commands.elementAt(1))));
			return false;
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
