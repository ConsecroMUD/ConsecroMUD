package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session.InputCallback;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;


@SuppressWarnings("rawtypes")
public class Password extends StdCommand
{
	public Password(){}

	private final String[] access=I(new String[]{"PASSWORD"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(final MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final PlayerStats pstats=mob.playerStats();
		if(pstats==null) return false;
		if(mob.isMonster()) return false;
		final Session sess=mob.session();
		if(sess!=null)
		sess.prompt(new InputCallback(InputCallback.Type.PROMPT)
		{
			@Override public void showPrompt() { sess.promptPrint(L("Enter your old password : ")); }
			@Override public void timedOut() { }
			@Override public void callBack()
			{
				final String old=this.input;
				sess.prompt(new InputCallback(InputCallback.Type.PROMPT)
				{
					@Override public void showPrompt() { sess.promptPrint(L("Enter a new password    : ")); }
					@Override public void timedOut() { }
					@Override public void callBack()
					{
						final String nep=this.input;
						sess.prompt(new InputCallback(InputCallback.Type.PROMPT)
						{
							@Override public void showPrompt() { sess.promptPrint(L("Enter new password again: ")); }
							@Override public void timedOut() { }
							@Override public void callBack()
							{
								final String ne2=this.input;
								if(!pstats.matchesPassword(old))
									mob.tell(L("Your old password was not entered correctly."));
								else
								if(!nep.equals(ne2))
									mob.tell(L("Your new password was not entered the same way twice!"));
								else
								{
									pstats.setPassword(nep);
									mob.tell(L("Your password has been changed."));
									if(pstats.getAccount()!=null)
										CMLib.database().DBUpdateAccount(pstats.getAccount());
									CMLib.database().DBUpdatePassword(mob.Name(),pstats.getPasswordStr());
								}
							}
						});
					}
				});
			}
		});
		return false;
	}

	@Override public boolean canBeOrdered(){return false;}


}
