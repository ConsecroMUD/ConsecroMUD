package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
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
public class Look extends StdCommand
{
	public Look(){}

	private final String[] access=I(new String[]{"LOOK","LOO","LO","L"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final Room R=mob.location();
		boolean quiet=false;
		if((commands!=null)&&(commands.size()>1)&&(((String)commands.lastElement()).equalsIgnoreCase("UNOBTRUSIVELY")))
		{
			commands.removeElementAt(commands.size()-1);
			quiet=true;
		}
		final String textMsg="<S-NAME> look(s) ";
		if(R==null) return false;
		if((commands!=null)&&(commands.size()>1))
		{
			Environmental thisThang=null;

			if((commands.size()>2)&&(((String)commands.elementAt(1)).equalsIgnoreCase("at")))
			   commands.removeElementAt(1);
			else
			if((commands.size()>2)&&(((String)commands.elementAt(1)).equalsIgnoreCase("to")))
			   commands.removeElementAt(1);
			final String ID=CMParms.combine(commands,1);

			if((ID.toUpperCase().startsWith("EXIT")&&(commands.size()==2))&&(CMProps.getIntVar(CMProps.Int.EXVIEW)!=1))
			{
				final CMMsg exitMsg=CMClass.getMsg(mob,R,null,CMMsg.MSG_LOOK_EXITS,null);
				if((CMProps.getIntVar(CMProps.Int.EXVIEW)>=2)!=mob.isAttribute(MOB.Attrib.BRIEF))
					exitMsg.setValue(CMMsg.MASK_OPTIMIZE);
				if(R.okMessage(mob, exitMsg))
					R.send(mob, exitMsg);
				return false;
			}
			if(ID.equalsIgnoreCase("SELF")||ID.equalsIgnoreCase("ME"))
				thisThang=mob;

			if(thisThang==null)
				thisThang=R.fetchFromMOBRoomFavorsItems(mob,null,ID, noCoinFilter);
			if(thisThang==null)
				thisThang=R.fetchFromMOBRoomFavorsItems(mob,null,ID,Wearable.FILTER_ANY);
			if((thisThang==null)
			&&(commands.size()>2)
			&&(((String)commands.elementAt(1)).equalsIgnoreCase("in")))
			{
				commands.removeElementAt(1);
				final String ID2=CMParms.combine(commands,1);
				thisThang=R.fetchFromMOBRoomFavorsItems(mob,null,ID2,Wearable.FILTER_ANY);
				if((thisThang!=null)&&((!(thisThang instanceof Container))||(((Container)thisThang).capacity()==0)))
				{
					mob.tell(L("That's not a container."));
					return false;
				}
			}
			int dirCode=-1;
			Environmental lookingTool=null;
			if(thisThang==null)
			{
				dirCode=Directions.getGoodDirectionCode(ID);
				if(dirCode>=0)
				{
					final Room room=R.getRoomInDir(dirCode);
					final Exit exit=R.getExitInDir(dirCode);
					if((room!=null)&&(exit!=null))
					{
						thisThang=exit;
						lookingTool=room;
					}
					else
					{
						mob.tell(L("You don't see anything that way."));
						return false;
					}
				}
			}
			if(thisThang!=null)
			{
				String name="at <T-NAMESELF>";
 				if((thisThang instanceof Room)||(thisThang instanceof Exit))
				{
					if(thisThang==R)
						name="around";
					else
					if(dirCode>=0)
						name=((R instanceof SpaceShip)||(R.getArea() instanceof SpaceShip))?
								Directions.getShipDirectionName(dirCode):Directions.getDirectionName(dirCode);
				}
				final CMMsg msg=CMClass.getMsg(mob,thisThang,lookingTool,CMMsg.MSG_LOOK,textMsg+name+".");
				if((thisThang instanceof Room)&&(mob.isAttribute(MOB.Attrib.AUTOEXITS))&&(CMProps.getIntVar(CMProps.Int.EXVIEW)!=1))
				{
					final CMMsg exitMsg=CMClass.getMsg(mob,thisThang,lookingTool,CMMsg.MSG_LOOK_EXITS,null);
					if((CMProps.getIntVar(CMProps.Int.EXVIEW)>=2)!=mob.isAttribute(MOB.Attrib.BRIEF))
						exitMsg.setValue(CMMsg.MASK_OPTIMIZE);
					msg.addTrailerMsg(exitMsg);
				}
				if(R.okMessage(mob,msg))
					R.send(mob,msg);
			}
			else
				mob.tell(L("You don't see that here!"));
		}
		else
		{
			if((commands!=null)&&(commands.size()>0))
				if(((String)commands.elementAt(0)).toUpperCase().startsWith("E"))
				{
					mob.tell(L("Examine what?"));
					return false;
				}

			final CMMsg msg=CMClass.getMsg(mob,R,null,CMMsg.MSG_LOOK,(quiet?null:textMsg+"around."),CMMsg.MSG_LOOK,(quiet?null:textMsg+"at you."),CMMsg.MSG_LOOK,(quiet?null:textMsg+"around."));
			if((mob.isAttribute(MOB.Attrib.AUTOEXITS))&&(CMProps.getIntVar(CMProps.Int.EXVIEW)!=1)&&(CMLib.flags().canBeSeenBy(R,mob)))
			{
				final CMMsg exitMsg=CMClass.getMsg(mob,R,null,CMMsg.MSG_LOOK_EXITS,null);
				if((CMProps.getIntVar(CMProps.Int.EXVIEW)>=2)!=mob.isAttribute(MOB.Attrib.BRIEF))
					exitMsg.setValue(CMMsg.MASK_OPTIMIZE);
				msg.addTrailerMsg(exitMsg);
			}
			if(R.okMessage(mob,msg))
				R.send(mob,msg);
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
}
