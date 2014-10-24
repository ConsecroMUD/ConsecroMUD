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


@SuppressWarnings("rawtypes")
public class Quit extends StdCommand
{
	public Quit(){}

	private final String[] access=I(new String[]{"QUIT","QUI","Q"});
	@Override public String[] getAccessWords(){return access;}

	public static void dispossess(MOB mob, boolean force)
	{
		if(mob.soulMate()==null)
		{
			mob.tell(CMLib.lang().L("Huh?"));
			return;
		}
		final CMMsg msg=CMClass.getMsg(mob, CMMsg.MSG_DISPOSSESS, CMLib.lang().L("^H<S-YOUPOSS> spirit has returned to <S-YOUPOSS> body...\n\r\n\r^N"));
		final Room room=mob.location();
		if((room==null)||(room.okMessage(mob, msg))||force)
		{
			if(room!=null) room.send(mob, msg);
			mob.dispossess(true);
		}
	}

	@Override
	public boolean execute(final MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.soulMate()!=null)
			dispossess(mob,CMParms.combine(commands).endsWith("!"));
		else
		if(!mob.isMonster())
		{
			final Session session=mob.session();
			if(session!=null)
			{
				if((session.getLastPKFight()>0)
				&&((System.currentTimeMillis()-session.getLastPKFight())<(5*60*1000)))
				{
					mob.tell(L("You must wait a few more minutes before you are allowed to quit."));
					return false;
				}
				session.prompt(new InputCallback(InputCallback.Type.CONFIRM, "N", 30000)
				{
					@Override
					public void showPrompt()
					{
						session.promptPrint(L("\n\rQuit -- are you sure (y/N)?"));
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
								session.stopSession(false,false, false); // this should call prelogout and later loginlogoutthread to cause msg SEND
								CMLib.commands().monitorGlobalMessage(R, msg);
							}
						}
					}
				});
			}
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return false;}


}
