package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.SpaceShip;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings("rawtypes")
public class Open extends StdCommand
{
	public Open(){}

	private final String[] access=I(new String[]{"OPEN","OP","O"});
	@Override public String[] getAccessWords(){return access;}

	private final static Class[][] internalParameters=new Class[][]{{Environmental.class,Boolean.class}};

	public boolean open(MOB mob, Environmental openThis, String openableWord, int dirCode, boolean quietly)
	{
		final String openWord=(!(openThis instanceof Exit))?"open":((Exit)openThis).openWord();
		final String openMsg=quietly?null:("<S-NAME> "+openWord+"(s) <T-NAMESELF>.")+CMLib.protocol().msp("dooropen.wav",10);
		final CMMsg msg=CMClass.getMsg(mob,openThis,null,CMMsg.MSG_OPEN,openMsg,openableWord,openMsg);
		if(openThis instanceof Exit)
		{
			final boolean open=((Exit)openThis).isOpen();
			if((mob.location().okMessage(msg.source(),msg))
			&&(!open))
			{
				mob.location().send(msg.source(),msg);

				if(dirCode<0)
				for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
					if(mob.location().getExitInDir(d)==openThis)
					{dirCode=d; break;}
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
					if((opE!=null)&&(opE.isOpen())&&(((Exit)openThis).isOpen()))
					{
						final boolean useShipDirs=(opR instanceof SpaceShip)||(opR.getArea() instanceof SpaceShip);
						final String inDirName=useShipDirs?Directions.getShipInDirectionName(opCode):Directions.getInDirectionName(opCode);
						opR.showHappens(CMMsg.MSG_OK_ACTION,L("@x1 @x2 opens.",opE.name(),inDirName));
					}
					return true;
				}
			}
		}
		else
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			return true;
		}
		return false;
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final String whatToOpen=CMParms.combine(commands,1);
		if(whatToOpen.length()==0)
		{
			mob.tell(L("Open what?"));
			return false;
		}
		Environmental openThis=null;
		final int dirCode=Directions.getGoodDirectionCode(whatToOpen);
		if(dirCode>=0)
			openThis=mob.location().getExitInDir(dirCode);
		if(openThis==null)
			openThis=mob.location().fetchFromMOBRoomItemExit(mob,null,whatToOpen,Wearable.FILTER_ANY);

		if((openThis==null)||(!CMLib.flags().canBeSeenBy(openThis,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",whatToOpen));
			return false;
		}
		open(mob,openThis,whatToOpen,dirCode,false);
		return false;
	}

	@Override
	public Object executeInternal(MOB mob, int metaFlags, Object... args) throws java.io.IOException
	{
		if(!super.checkArguments(internalParameters, args))
			return Boolean.FALSE;
		return Boolean.valueOf(open(mob,(Environmental)args[0],((Environmental)args[0]).name(),-1,((Boolean)args[1]).booleanValue()));
	}

	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
