package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Iterator;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.WorldMap;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.GridLocale;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.Log;


@SuppressWarnings("rawtypes")
public class Link extends At
{
	public Link(){}

	private final String[] access=I(new String[]{"LINK"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("^S<S-NAME> wave(s) <S-HIS-HER> arms...^?"));

		if(commands.size()<3)
		{
			mob.tell(L("You have failed to specify the proper fields.\n\rThe format is LINK [ROOM ID] [DIRECTION]\n\r"));
			mob.location().showOthers(mob,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> flub(s) a powerful spell."));
			return false;
		}
		final String dirStr=(String)commands.lastElement();
		commands.removeElementAt(commands.size()-1);
		final int direction=Directions.getGoodDirectionCode(dirStr);
		if(direction<0)
		{
			mob.tell(L("You have failed to specify a direction.  Try @x1.\n\r",Directions.LETTERS()));
			mob.location().showOthers(mob,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> flub(s) a powerful spell."));
			return false;
		}

		Room thisRoom=null;
		final String RoomID=CMParms.combine(commands,1);
		thisRoom=CMLib.map().getRoom(RoomID);
		if(thisRoom==null)
		{
			thisRoom=CMLib.map().findWorldRoomLiberally(mob,RoomID,"R",100,120000);
			if(thisRoom==null)
			{
				mob.tell(L("Room \"@x1\" is unknown.  Try again.",RoomID));
				mob.location().showOthers(mob,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> flub(s) a powerful spell."));
				return false;
			}
		}
		exitifyNewPortal(mob,thisRoom,direction);
		mob.location().getArea().fillInAreaRoom(mob.location());
		mob.location().getArea().fillInAreaRoom(thisRoom);

		mob.location().recoverRoomStats();
		mob.location().showHappens(CMMsg.MSG_OK_ACTION,L("Suddenly a portal opens up in the landscape.\n\r"));
		Log.sysOut("Link",mob.Name()+" linked "+CMLib.map().getExtendedRoomID(mob.location())+" to room "+CMLib.map().getExtendedRoomID(thisRoom)+".");
		return false;
	}

	protected void exitifyNewPortal(MOB mob, Room room, int direction)
	{
		Room opRoom=mob.location().rawDoors()[direction];
		if((opRoom!=null)&&(opRoom.roomID().length()==0))
			opRoom=null;
		Room reverseRoom=null;
		final int opDir=Directions.getOpDirectionCode(direction);
		if(opRoom!=null)
			reverseRoom=opRoom.rawDoors()[opDir];

		if((reverseRoom!=null)
		&&((reverseRoom==mob.location())||(reverseRoom==mob.location().getGridParent())))
			mob.tell(L("Opposite room already exists and heads this way.  One-way link created."));

		if(opRoom!=null)
			mob.location().rawDoors()[direction]=null;

		WorldMap.CrossExit CE=null;
		final GridLocale hereGL=(mob.location().getGridParent()!=null)?mob.location().getGridParent():null;
		final int hereX=(hereGL!=null)?hereGL.getGridChildX(mob.location()):-1;
		final int hereY=(hereGL!=null)?hereGL.getGridChildY(mob.location()):-1;
		final GridLocale thereGL=(room.getGridParent()!=null)?room.getGridParent():null;
		final int thereX=(thereGL!=null)?thereGL.getGridChildX(room):-1;
		final int thereY=(thereGL!=null)?thereGL.getGridChildY(room):-1;
		if(hereGL!=null)
		{
			for(final Iterator<WorldMap.CrossExit> hereIter=hereGL.outerExits();hereIter.hasNext();)
			{
				CE=hereIter.next();
				if((CE.out)
				&&(CE.dir==direction)
				&&(CE.x==hereX)&&(CE.y==hereY))
				   hereGL.delOuterExit(CE);
			}
			CE=WorldMap.CrossExit.make(hereX,hereY,direction,CMLib.map().getExtendedRoomID(room),true);
			hereGL.addOuterExit(CE);
		}

		if(thereGL!=null)
			mob.location().rawDoors()[direction]=thereGL;
		else
			mob.location().rawDoors()[direction]=room;

		Exit thisExit=mob.location().getRawExit(direction);
		if(thisExit==null)
		{
			thisExit=CMClass.getExit("StdOpenDoorway");
			mob.location().setRawExit(direction,thisExit);
		}
		if(thereGL!=null)
		{
			for(final Iterator<WorldMap.CrossExit> thereIter=thereGL.outerExits();thereIter.hasNext();)
			{
				CE=thereIter.next();
				if((!CE.out)
				&&(CE.dir==direction)
				&&(CE.destRoomID.equals(CMLib.map().getExtendedRoomID(mob.location()))))
				   thereGL.delOuterExit(CE);
			}
			CE=WorldMap.CrossExit.make(thereX,thereY,direction,CMLib.map().getExtendedRoomID(mob.location()),false);
			thereGL.addOuterExit(CE);

			if((room.rawDoors()[opDir]==null)
			||(thereGL==room.rawDoors()[opDir])
			||(thereGL.isMyGridChild(room.rawDoors()[opDir])))
			{
				for(final Iterator<WorldMap.CrossExit> thereIter=thereGL.outerExits();thereIter.hasNext();)
				{
					CE=thereIter.next();
					if((CE.out)
					&&(CE.dir==opDir)
					&&(CE.x==thereX)&&(CE.y==thereY))
					   thereGL.delOuterExit(CE);
				}
				CE=WorldMap.CrossExit.make(thereX,thereY,opDir,CMLib.map().getExtendedRoomID(mob.location()),true);
				thereGL.addOuterExit(CE);
				if(hereGL!=null)
				{
					room.rawDoors()[opDir]=hereGL;
					for(final Iterator<WorldMap.CrossExit> hereIter=hereGL.outerExits();hereIter.hasNext();)
					{
						CE=hereIter.next();
						if((!CE.out)
						&&(CE.dir==opDir)
						&&(CE.destRoomID.equals(CMLib.map().getExtendedRoomID(room))))
						   hereGL.delOuterExit(CE);
					}
					CE=WorldMap.CrossExit.make(hereX,hereY,opDir,CMLib.map().getExtendedRoomID(room),false);
					hereGL.addOuterExit(CE);
				}
				else
					room.rawDoors()[opDir]=mob.location();
				room.setRawExit(opDir,thisExit);
			}
		}
		else
		if(room.rawDoors()[opDir]==null)
		{
			if(hereGL!=null)
			{
				room.rawDoors()[opDir]=hereGL;
				for(final Iterator<WorldMap.CrossExit> hereIter=hereGL.outerExits();hereIter.hasNext();)
				{
					CE=hereIter.next();
					if((!CE.out)
					&&(CE.dir==opDir)
					&&(CE.destRoomID.equals(room.roomID())))
					   hereGL.delOuterExit(CE);
				}
				CE=WorldMap.CrossExit.make(hereX,hereY,opDir,CMLib.map().getExtendedRoomID(room),false);
				hereGL.addOuterExit(CE);
			}
			else
				room.rawDoors()[opDir]=mob.location();
			room.setRawExit(opDir,thisExit);
		}
		if(hereGL!=null)
			CMLib.database().DBUpdateExits(hereGL);
		else
			CMLib.database().DBUpdateExits(mob.location());
		if(thereGL!=null)
			CMLib.database().DBUpdateExits(thereGL);
		else
			CMLib.database().DBUpdateExits(room);
	}



	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CMDEXITS);}


}
