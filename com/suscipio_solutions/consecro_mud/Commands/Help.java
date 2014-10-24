package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.Resources;


@SuppressWarnings("rawtypes")
public class Help extends StdCommand
{
	public Help(){}

	private final String[] access=I(new String[]{"HELP"});
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
		StringBuilder thisTag=null;
		if(helpStr.length()==0)
			thisTag=new StringBuilder(Resources.getFileResource("help/help.txt",true));
		else
			thisTag=CMLib.help().getHelpText(helpStr,CMLib.help().getHelpFile(),mob);
		if((thisTag==null)&&(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.IMMHELP)))
			thisTag=CMLib.help().getHelpText(helpStr,CMLib.help().getImmHelpFile(),mob);
		if(thisTag==null)
		{
			final StringBuilder thisList=
				CMLib.help().getHelpList(
				helpStr,
				CMLib.help().getHelpFile(),
				CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.IMMHELP)?CMLib.help().getImmHelpFile():null,
				mob);
			if((thisList!=null)&&(thisList.length()>0))
				mob.tell(L("No help is available on '@x1'.\n\rHowever, here are some search matches:\n\r^N@x2",helpStr,thisList.toString().replace('_',' ')));
			else
				mob.tell(L("No help is available on '@x1'.\n\rEnter 'COMMANDS' for a command list, or 'TOPICS' for a complete list, or 'HELPLIST' to search.",helpStr));
			Log.helpOut("Help",mob.Name()+" wanted help on "+helpStr);
		}
		else
		if(!mob.isMonster())
			mob.session().wraplessPrintln(thisTag.toString());
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
