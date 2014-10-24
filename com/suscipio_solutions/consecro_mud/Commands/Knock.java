package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.SpaceShip;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings({"unchecked","rawtypes"})
public class Knock extends StdCommand
{
	public Knock(){}

	private final String[] access=I(new String[]{"KNOCK"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<=1)
		{
			mob.tell(L("Knock on what?"));
			return false;
		}
		final String knockWhat=CMParms.combine(commands,1).toUpperCase();
		final int dir=CMLib.tracking().findExitDir(mob,mob.location(),knockWhat);
		if(dir<0)
		{
			final Environmental getThis=mob.location().fetchFromMOBRoomItemExit(mob,null,knockWhat,Wearable.FILTER_UNWORNONLY);
			if(getThis==null)
			{
				mob.tell(L("You don't see '@x1' here.",knockWhat.toLowerCase()));
				return false;
			}
			final CMMsg msg=CMClass.getMsg(mob,getThis,null,CMMsg.MSG_KNOCK,CMMsg.MSG_KNOCK,CMMsg.MSG_KNOCK,L("<S-NAME> knock(s) on <T-NAMESELF>.@x1",CMLib.protocol().msp("knock.wav",50)));
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);

		}
		else
		{
			Exit E=mob.location().getExitInDir(dir);
			if(E==null)
			{
				mob.tell(L("Knock on what?"));
				return false;
			}
			if(!E.hasADoor())
			{
				mob.tell(L("You can't knock on @x1!",E.name()));
				return false;
			}
			final CMMsg msg=CMClass.getMsg(mob,E,null,CMMsg.MSG_KNOCK,CMMsg.MSG_KNOCK,CMMsg.MSG_KNOCK,L("<S-NAME> knock(s) on <T-NAMESELF>.@x1",CMLib.protocol().msp("knock.wav",50)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				E=mob.location().getPairedExit(dir);
				final Room R=mob.location().getRoomInDir(dir);
				if((R!=null)&&(E!=null)&&(E.hasADoor())
				&&(R.showOthers(mob,E,null,CMMsg.MSG_KNOCK,L("You hear a knock on <T-NAMESELF>.@x1",CMLib.protocol().msp("knock.wav",50))))
				&&((R.domainType()&Room.INDOORS)==Room.INDOORS))
				{
					final Vector V=new Vector();
					V.addElement(mob.location());
					TrackingLibrary.TrackingFlags flags;
					flags = new TrackingLibrary.TrackingFlags()
							.plus(TrackingLibrary.TrackingFlag.OPENONLY);
					CMLib.tracking().getRadiantRooms(R,V,flags,null,5,null);
					V.removeElement(mob.location());
					for(int v=0;v<V.size();v++)
					{
						final Room R2=(Room)V.elementAt(v);
						final int dir2=CMLib.tracking().radiatesFromDir(R2,V);
						if((dir2>=0)&&((R2.domainType()&Room.INDOORS)==Room.INDOORS))
						{
							final Room R3=R2.getRoomInDir(dir2);
							if(((R3!=null)&&(R3.domainType()&Room.INDOORS)==Room.INDOORS))
							{
								final boolean useShipDirs=(R2 instanceof SpaceShip)||(R2.getArea() instanceof SpaceShip);
								final String inDirName=useShipDirs?Directions.getShipInDirectionName(dir2):Directions.getInDirectionName(dir2);
								R2.showHappens(CMMsg.MASK_SOUND|CMMsg.TYP_KNOCK,L("You hear a knock @x1.@x2",inDirName,CMLib.protocol().msp("knock.wav",50)));
							}
						}
					}
				}
			}
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
