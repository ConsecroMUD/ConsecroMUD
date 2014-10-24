package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session.InputCallback;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Log;


@SuppressWarnings("rawtypes")
public class Retire extends StdCommand
{
	public Retire(){}

	private final String[] access=I(new String[]{"RETIRE"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(final MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final Session session=mob.session();
		if(session==null) return false;
		final PlayerStats pstats=mob.playerStats();
		if(pstats==null) return false;

		mob.tell(L("^HThis will delete your player from the system FOREVER!"));
		session.prompt(new InputCallback(InputCallback.Type.PROMPT,"",120000)
		{
			@Override public void showPrompt()
			{
				session.promptPrint(L("If that's what you want, re-enter your password: "));
			}
			@Override public void timedOut() {}
			@Override public void callBack()
			{
				if(input.trim().length()==0)
					return;
				if(!pstats.matchesPassword(input.trim()))
					mob.tell(L("Password incorrect."));
				else
				{
					if(CMSecurity.isDisabled(CMSecurity.DisFlag.RETIREREASON))
					{
						Log.sysOut("Retire","Retired: "+mob.Name());
						CMLib.players().obliteratePlayer(mob,true,false);
						session.logout(true);
					}
					else
					session.prompt(new InputCallback(InputCallback.Type.PROMPT,"")
					{
						@Override public void showPrompt()
						{
							session.promptPrint(L("OK.  Please leave us a short message as to why you are deleting this character.  Your answers will be kept confidential, and are for administrative purposes only.\n\r: "));
						}
						@Override public void timedOut() {}
						@Override public void callBack()
						{
							Log.sysOut("Retire","Retired: "+mob.Name()+": "+this.input);
							CMLib.players().obliteratePlayer(mob,true,false);
							session.logout(true);
						}
					});
				}
			}
		});
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return false;}


}
