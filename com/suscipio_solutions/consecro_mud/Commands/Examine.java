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
public class Examine extends StdCommand
{
	public Examine(){}

	private final String[] access=I(new String[]{"EXAMINE","EXAM","EXA","LONGLOOK","LLOOK","LL"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		boolean quiet=false;
		if((commands!=null)&&(commands.size()>1)&&(((String)commands.lastElement()).equalsIgnoreCase("UNOBTRUSIVELY")))
		{
			commands.removeElementAt(commands.size()-1);
			quiet=true;
		}
		final String textMsg="<S-NAME> examine(s) ";
		if(mob.location()==null) return false;
		if((commands!=null)&&(commands.size()>1))
		{
			Environmental thisThang=null;

			final String ID=CMParms.combine(commands,1);
			if(ID.length()==0)
				thisThang=mob.location();
			else
			if((ID.toUpperCase().startsWith("EXIT")&&(commands.size()==2)))
			{
				final CMMsg exitMsg=CMClass.getMsg(mob,thisThang,null,CMMsg.MSG_LOOK_EXITS,null);
				if((CMProps.getIntVar(CMProps.Int.EXVIEW)>=2)!=mob.isAttribute(MOB.Attrib.BRIEF))
					exitMsg.setValue(CMMsg.MASK_OPTIMIZE);
				if(mob.location().okMessage(mob, exitMsg))
					mob.location().send(mob, exitMsg);
				return false;
			}
			if(ID.equalsIgnoreCase("SELF")||ID.equalsIgnoreCase("ME"))
				thisThang=mob;

			if(thisThang==null)
				thisThang=mob.location().fetchFromMOBRoomFavorsItems(mob,null,ID,Wearable.FILTER_ANY);
			int dirCode=-1;
			if(thisThang==null)
			{
				dirCode=Directions.getGoodDirectionCode(ID);
				if(dirCode>=0)
				{
					final Room room=mob.location().getRoomInDir(dirCode);
					final Exit exit=mob.location().getExitInDir(dirCode);
					if((room!=null)&&(exit!=null))
						thisThang=exit;
					else
					{
						mob.tell(L("You don't see anything that way."));
						return false;
					}
				}
			}
			if(thisThang!=null)
			{
				String name="<T-NAMESELF>";
				if((thisThang instanceof Room)||(thisThang instanceof Exit))
				{
					if(thisThang==mob.location())
						name="around";
					else
					if(dirCode>=0)
						name=((mob.location() instanceof SpaceShip)||(mob.location().getArea() instanceof SpaceShip))?
							Directions.getShipDirectionName(dirCode):Directions.getDirectionName(dirCode);
				}
				final CMMsg msg=CMClass.getMsg(mob,thisThang,null,CMMsg.MSG_EXAMINE,L("@x1@x2 closely.",textMsg,name));
				if(mob.location().okMessage(mob,msg))
					mob.location().send(mob,msg);
				if((mob.isAttribute(MOB.Attrib.AUTOEXITS))&&(thisThang instanceof Room))
					msg.addTrailerMsg(CMClass.getMsg(mob,thisThang,null,CMMsg.MSG_LOOK_EXITS,null));
			}
			else
				mob.tell(L("You don't see that here!"));
		}
		else
		{
			final CMMsg msg=CMClass.getMsg(mob,mob.location(),null,CMMsg.MSG_EXAMINE,(quiet?null:textMsg+"around carefully."),CMMsg.MSG_EXAMINE,(quiet?null:textMsg+"at you."),CMMsg.MSG_EXAMINE,(quiet?null:textMsg+"around carefully."));
			if((mob.isAttribute(MOB.Attrib.AUTOEXITS))&&(CMLib.flags().canBeSeenBy(mob.location(),mob)))
				msg.addTrailerMsg(CMClass.getMsg(mob,mob.location(),null,CMMsg.MSG_LOOK_EXITS,null));
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(MOB mob, List<String> cmds){return 1.0;}
	@Override public boolean canBeOrdered(){return true;}
}
