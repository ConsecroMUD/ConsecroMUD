package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session.InputCallback;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings("rawtypes")
public class Title extends StdCommand
{
	private final String[] access=I(new String[]{"TITLE"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(final MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if((mob.playerStats()==null)||(mob.playerStats().getTitles().size()==0))
		{
			mob.tell(L("You don't have any titles to select from."));
			return false;
		}
		final String currTitle=mob.playerStats().getTitles().get(0);
		if(currTitle.startsWith("{")&&currTitle.endsWith("}"))
		{
			mob.tell(L("You can not change your current title."));
			return false;
		}
		final PlayerStats ps=mob.playerStats();
		final StringBuffer menu=new StringBuffer("^xTitles:^.^?\n\r");
		CMLib.titles().evaluateAutoTitles(mob);
		if(!ps.getTitles().contains("*"))
			ps.getTitles().add("*");
		for(int i=0;i<ps.getTitles().size();i++)
		{
			String title=ps.getTitles().get(i);
			if(title.startsWith("{")&&title.endsWith("}")) title=title.substring(1,title.length()-1);
			if(title.equalsIgnoreCase("*"))
				menu.append(CMStrings.padRight(""+(i+1),2)+": Do not use a title.\n\r");
			else
				menu.append(CMStrings.padRight(""+(i+1),2)+": "+CMStrings.replaceAll(title,"*",mob.Name())+"\n\r");
		}
		final InputCallback[] IC=new InputCallback[1];
		IC[0]=new InputCallback(InputCallback.Type.PROMPT,"")
		{
			@Override public void showPrompt()
			{
				mob.tell(menu.toString());
				if(mob.session()!=null)
					mob.session().promptPrint(L("Enter a selection: "));
			}
			@Override public void timedOut() {}
			@Override public void callBack()
			{
				final int num=CMath.s_int(this.input);
				if((num>0)&&(num<=ps.getTitles().size()))
				{
					final String which=ps.getTitles().get(num-1);
					ps.getTitles().remove(num-1);
					ps.getTitles().add(0,which);
					mob.tell(L("Title changed accepted."));
				}
				else
					mob.tell(L("No change"));
			}
		};
		if(mob.session()!=null)
			mob.session().prompt(IC[0]);
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
}

