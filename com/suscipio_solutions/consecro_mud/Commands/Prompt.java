package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;


@SuppressWarnings("rawtypes")
public class Prompt extends StdCommand
{
	public Prompt(){}

	private final String[] access=I(new String[]{"PROMPT"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.session()==null) return false;
		final PlayerStats pstats=mob.playerStats();
		final Session sess=mob.session();
		if(pstats==null) return false;

		if(commands.size()==1)
			sess.rawPrintln(L("Your prompt is currently set at:\n\r@x1",pstats.getPrompt()));
		else
		{
			String str=CMParms.combine(commands,1);
			String showStr=str;
			if(("DEFAULT").startsWith(str.toUpperCase()))
			{
				str="";
				showStr=CMProps.getVar(CMProps.Str.DEFAULTPROMPT);
			}
			if(sess.confirm(L("Change your prompt to: @x1, are you sure (Y/n)?",showStr),L("Y")))
			{
				pstats.setPrompt(str);
				sess.rawPrintln(L("Your prompt is currently now set at:\n\r@x1",pstats.getPrompt()));
			}
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return false;}


}
