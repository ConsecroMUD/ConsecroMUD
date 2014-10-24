package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Log;


@SuppressWarnings("rawtypes")
public class HelpList extends StdCommand
{
	public HelpList(){}

	private final String[] access=I(new String[]{"HELPLIST","HLIST"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final String helpStr=CMParms.combine(commands,1);
		if(CMLib.help().getHelpFile().size()==0)
		{
			mob.tell(L("No help is available."));
			return false;
		}
		if(helpStr.length()==0)
		{
			mob.tell(L("You must enter a search pattern.  Use 'TOPICS' or 'COMMANDS' for an unfiltered list."));
			return false;
		}
		final StringBuilder thisTag=
					CMLib.help().getHelpList(
					helpStr,
					CMLib.help().getHelpFile(),
					CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.IMMHELP)?CMLib.help().getImmHelpFile():null,
					mob);
		if((thisTag==null)||(thisTag.length()==0))
		{
			mob.tell(L("No help entries match '@x1'.\nEnter 'COMMANDS' for a command list, or 'TOPICS' for a complete list.",helpStr));
			Log.helpOut("Help",mob.Name()+" wanted help list match on "+helpStr);
		}
		else
		if(!mob.isMonster())
			mob.session().wraplessPrintln(L("^xHelp File Matches:^.^?\n\r^N@x1",thisTag.toString().replace('_',' ')));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}

