package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session.InputCallback;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Log;


@SuppressWarnings("rawtypes")
public class Logoff extends StdCommand
{
	public Logoff(){}

	private final String[] access=I(new String[]{"LOGOFF","LOGOUT"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(final MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.soulMate()!=null)
			Quit.dispossess(mob,CMParms.combine(commands).endsWith("!"));
		else
		if(!mob.isMonster())
		{
			final Session session=mob.session();
			if((session!=null)
			&&(session.getLastPKFight()>0)
			&&((System.currentTimeMillis()-session.getLastPKFight())<(5*60*1000)))
			{
				mob.tell(L("You must wait a few more minutes before you are allowed to logout."));
				return false;
			}
			try
			{
				if(session != null)
					session.prompt(new InputCallback(InputCallback.Type.CONFIRM, "N", 30000)
					{
						@Override
						public void showPrompt()
						{
							session.promptPrint(L("\n\rLogout -- are you sure (y/N)?"));
						}
						@Override public void timedOut() {}

						@Override
						public void callBack()
						{
							if(this.confirmed)
							{
								final CMMsg msg=CMClass.getMsg(mob,null,CMMsg.MSG_QUIT,null);
								final Room R=mob.location();
								if((R!=null)&&(R.okMessage(mob,msg)))
								{
									CMLib.map().sendGlobalMessage(mob,CMMsg.TYP_QUIT, CMClass.getMsg(mob,null,CMMsg.MSG_QUIT,null));
									session.logout(true); // this should call prelogout and later loginlogoutthread to cause msg SEND
									CMLib.commands().monitorGlobalMessage(R, msg);
								}
							}
						}
					});
			}
			catch(final Exception e)
			{
				Log.errOut("Logoff",e.getMessage());
			}
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return false;}


}
