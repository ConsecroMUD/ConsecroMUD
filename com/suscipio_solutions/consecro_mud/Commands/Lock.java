package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.SpaceShip;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.CloseableLockable;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings("rawtypes")
public class Lock extends StdCommand
{
	public Lock(){}

	private final String[] access=I(new String[]{"LOCK","LOC"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final String whatTolock=CMParms.combine(commands,1);
		if(whatTolock.length()==0)
		{
			mob.tell(L("Lock what?"));
			return false;
		}
		Environmental lockThis=null;
		int dirCode=Directions.getGoodDirectionCode(whatTolock);
		if(dirCode>=0)
			lockThis=mob.location().getExitInDir(dirCode);
		if(lockThis==null)
			lockThis=mob.location().fetchFromMOBRoomItemExit(mob,null,whatTolock,Wearable.FILTER_ANY);

		if((lockThis==null)||(!CMLib.flags().canBeSeenBy(lockThis,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",whatTolock));
			return false;
		}
		if(lockThis instanceof CloseableLockable)
		{
			final CloseableLockable cLock=(CloseableLockable)lockThis;
			if(cLock.hasADoor() && cLock.isOpen())
			{
				Command C=CMClass.getCommand("Close");
				if(!((Boolean)C.executeInternal(mob, metaFlags, lockThis, whatTolock, Integer.valueOf(dirCode))).booleanValue())
					return false;
			}
		}
		final String lockMsg="<S-NAME> lock(s) <T-NAMESELF>."+CMLib.protocol().msp("doorlock.wav",10);
		final CMMsg msg=CMClass.getMsg(mob,lockThis,null,CMMsg.MSG_LOCK,lockMsg,whatTolock,lockMsg);
		if(lockThis instanceof Exit)
		{
			final boolean locked=((Exit)lockThis).isLocked();
			if((mob.location().okMessage(msg.source(),msg))
			&&(!locked))
			{
				mob.location().send(msg.source(),msg);
				if(dirCode<0)
				for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
					if(mob.location().getExitInDir(d)==lockThis)
					{
						dirCode=d; 
						break;
					}

				if((dirCode>=0)&&(mob.location().getRoomInDir(dirCode)!=null))
				{
					final Room opR=mob.location().getRoomInDir(dirCode);
					final Exit opE=mob.location().getPairedExit(dirCode);
					if(opE!=null)
					{
						final CMMsg altMsg=CMClass.getMsg(msg.source(),opE,msg.tool(),msg.sourceCode(),null,msg.targetCode(),null,msg.othersCode(),null);
						opE.executeMsg(msg.source(),altMsg);
					}
					final int opCode=Directions.getOpDirectionCode(dirCode);
					if((opE!=null)
					&&(opE.isLocked())
					&&(((Exit)lockThis).isLocked()))
					{
						final boolean useShipDirs=(opR instanceof SpaceShip)||(opR.getArea() instanceof SpaceShip);
						final String inDirName=useShipDirs?Directions.getShipInDirectionName(opCode):Directions.getInDirectionName(opCode);
						opR.showHappens(CMMsg.MSG_OK_ACTION,L("@x1 @x2 is locked from the other side.",opE.name(),inDirName));
					}
				}
			}
		}
		else
		if(mob.location().okMessage(mob,msg))
			mob.location().send(mob,msg);
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
