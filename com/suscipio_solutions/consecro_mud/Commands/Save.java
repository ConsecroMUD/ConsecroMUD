package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Resources;


@SuppressWarnings("rawtypes")
public class Save extends StdCommand
{
	public Save(){}

	private final String[] access=I(new String[]{"SAVE"});
	@Override public String[] getAccessWords(){return access;}

	public void clearSaveAndRestart(Room room, int taskCode)
	{
		synchronized(("SYNC"+room.roomID()).intern())
		{
			room=CMLib.map().getRoom(room);
			CMLib.threads().clearDebri(room,0);
			if(taskCode<2)
			{
				CMLib.database().DBUpdateItems(room);
				room.startItemRejuv();
			}
			if((taskCode==0)||(taskCode==2))
				CMLib.database().DBUpdateMOBs(room);
		}
	}


	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if((commands.size()==1)&&CMSecurity.isSaveFlag("NOPLAYERS"))
		{
			if(!mob.isMonster())
			{
				CMLib.database().DBUpdatePlayer(mob);
				CMLib.database().DBUpdateFollowers(mob);
				mob.tell(L("Your player record has been updated."));
			}
			return false;
		}

		mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("^S<S-NAME> wave(s) <S-HIS-HER> arms...^?"));
		String firstCommand="";
		String lastCommand = "";
		if(commands.size()>1)
		{
			firstCommand=((String)commands.elementAt(1)).toUpperCase();
			lastCommand=((String)commands.lastElement()).toUpperCase();
		}

		if(lastCommand.equals("USERS")||lastCommand.equals("PLAYERS")||lastCommand.equals("CHARACTERS"))
		{
			if(!CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CMDROOMS))
			{
				mob.tell(L("You are not allowed to save players."));
				return false;
			}
			for(final Session S : CMLib.sessions().allIterable())
			{
				final MOB M=S.mob();
				if(M!=null)
				{
					CMLib.database().DBUpdatePlayer(M);
					CMLib.database().DBUpdateFollowers(M);
				}
			}
			mob.location().showHappens(CMMsg.MSG_OK_ACTION,L("A feeling of permanency envelopes everyone.\n\r"));
		}
		else
		if(lastCommand.equals("ITEMS"))
		{
			if(!CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CMDROOMS))
			{
				mob.tell(L("You are not allowed to save the mobs here."));
				return false;
			}
			if(firstCommand.equals("AREA"))
			{
				if((mob.session()!=null)&&(mob.session().confirm(L("Doing this assumes every item in every room in this area is correctly placed.  Are you sure (N/y)?"),L("N"))))
				{
					final Area A=mob.location().getArea();
					for(final Enumeration<Room> e=A.getProperMap();e.hasMoreElements();)
						clearSaveAndRestart(e.nextElement(),1);
					mob.location().showHappens(CMMsg.MSG_OK_ACTION,L("A feeling of permanency envelopes the area.\n\r"));
				}
				else
					return false;
			}
			else
			{
				clearSaveAndRestart(mob.location(),1);
				mob.location().showHappens(CMMsg.MSG_OK_ACTION,L("A feeling of permanency envelopes the room.\n\r"));
			}
			Resources.removeResource("HELP_"+mob.location().getArea().Name().toUpperCase());
		}
		else
		if(lastCommand.equals("ROOM"))
		{
			if(!CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CMDROOMS))
			{
				mob.tell(L("You are not allowed to save the contents here."));
				return false;
			}
			if(firstCommand.equals("AREA"))
			{
				if((mob.session()!=null)&&(mob.session().confirm(L("Doing this assumes every mob and item in every room in this area is correctly placed.  Are you sure (N/y)?"),L("N"))))
				{
					final Area A=mob.location().getArea();
					for(final Enumeration e=A.getProperMap();e.hasMoreElements();)
						clearSaveAndRestart((Room)e.nextElement(),0);
					mob.location().showHappens(CMMsg.MSG_OK_ACTION,L("A feeling of permanency envelopes the area.\n\r"));
				}
				else
					return false;
			}
			else
			{
				clearSaveAndRestart(mob.location(),0);
				mob.location().show(mob,null,CMMsg.MSG_OK_ACTION,L("A feeling of permanency envelopes the room.\n\r"));
			}
			Resources.removeResource("HELP_"+mob.location().getArea().Name().toUpperCase());
		}
		else
		if(lastCommand.equals("MOBS"))
		{
			if(!CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CMDROOMS))
			{
				mob.tell(L("You are not allowed to save the mobs here."));
				return false;
			}
			if(firstCommand.equals("AREA"))
			{
				if((mob.session()!=null)&&(mob.session().confirm(L("Doing this assumes every mob in every room in this area is correctly placed.  Are you sure (N/y)?"),L("N"))))
				{
					final Area A=mob.location().getArea();
					for(final Enumeration e=A.getProperMap();e.hasMoreElements();)
						clearSaveAndRestart((Room)e.nextElement(),2);
					mob.location().showHappens(CMMsg.MSG_OK_ACTION,L("A feeling of permanency envelopes the area.\n\r"));
				}
				else
					return false;

			}
			else
			{
				clearSaveAndRestart(mob.location(),2);
				mob.location().showHappens(CMMsg.MSG_OK_ACTION,L("A feeling of permanency envelopes the room.\n\r"));
			}
			Resources.removeResource("HELP_"+mob.location().getArea().Name().toUpperCase());
		}
		else
		if(firstCommand.equals("QUESTS")||lastCommand.equals("QUESTS"))
		{
			if(!CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CMDQUESTS))
			{
				mob.tell(L("You are not allowed to save the contents here."));
				return false;
			}
			CMLib.quests().save();
			mob.tell(L("Quest list saved."));
		}
		else
		if(firstCommand.equals("USER")||firstCommand.equals("PLAYER")||firstCommand.equals("CHARACTER")||firstCommand.equals("CHAR"))
		{
			final MOB M=CMLib.players().getPlayer(lastCommand);
			if(M==null)
			{
				mob.tell(L("No user named @x1",lastCommand));
				return false;
			}
			CMLib.database().DBUpdatePlayer(M);
			if(CMLib.flags().isInTheGame(M,true))
				CMLib.database().DBUpdateFollowers(M);
			mob.location().showHappens(CMMsg.MSG_OK_ACTION,L("A feeling of permanency envelopes '@x1'.\n\r",M.name()));
		}
		else
		if(CMLib.players().getPlayer(firstCommand)!=null)
		{
			final MOB M=CMLib.players().getPlayer(firstCommand);
			CMLib.database().DBUpdatePlayer(M);
			if(CMLib.flags().isInTheGame(M,true))
				CMLib.database().DBUpdateFollowers(M);
			mob.location().showHappens(CMMsg.MSG_OK_ACTION,L("A feeling of permanency envelopes '@x1'.\n\r",M.name()));
		}
		else
		if(CMLib.players().getPlayer(lastCommand)!=null)
		{
			final MOB M=CMLib.players().getPlayer(lastCommand);
			CMLib.database().DBUpdatePlayer(M);
			if(CMLib.flags().isInTheGame(M,true))
				CMLib.database().DBUpdateFollowers(M);
			mob.location().showHappens(CMMsg.MSG_OK_ACTION,L("A feeling of permanency envelopes '@x1'.\n\r",M.name()));
		}
		else
		{
			mob.tell(
				L("\n\rYou cannot save '@x1'. However, you might try ITEMS, USERS, [PLAYERNAME], QUESTS, MOBS, or ROOM.",firstCommand));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override
	public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CMDROOMS)
												 ||CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CMDPLAYERS)
												 ||CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CMDQUESTS)
												 ||CMSecurity.isSaveFlag("NOPLAYERS");}


}
