package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
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
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;


@SuppressWarnings("rawtypes")
public class Pull extends Go
{
	public Pull(){}

	private final String[] access=I(new String[]{"PULL"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{

		Environmental openThis=null;
		String dir="";
		int dirCode=-1;
		Environmental E=null;
		if(commands.size()>1)
		{
			dirCode=Directions.getGoodDirectionCode((String)commands.lastElement());
			if(dirCode>=0)
			{
				if((mob.location().getRoomInDir(dirCode)==null)
				||(mob.location().getExitInDir(dirCode)==null)
				||(!mob.location().getExitInDir(dirCode).isOpen()))
				{
					mob.tell(L("You can't pull anything that way."));
					return false;
				}
				E=mob.location().getRoomInDir(dirCode);
				dir=" "+(((mob.location() instanceof SpaceShip)||(mob.location().getArea() instanceof SpaceShip))?
						Directions.getShipDirectionName(dirCode):Directions.getDirectionName(dirCode));
				commands.removeElementAt(commands.size()-1);
			}
		}
		if(dir.length()==0)
		{
			dirCode=Directions.getGoodDirectionCode((String)commands.lastElement());
			if(dirCode>=0)
				openThis=mob.location().getExitInDir(dirCode);
		}
		final String itemName=CMParms.combine(commands,1);
		if(openThis==null)
			openThis=mob.location().fetchFromRoomFavorItems(null,itemName);
		if(openThis==null)
			openThis=mob.location().fetchFromMOBRoomFavorsItems(mob,null,itemName,Wearable.FILTER_ANY);
		if((openThis==null)||(!CMLib.flags().canBeSeenBy(openThis,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",itemName));
			return false;
		}
		final CMMsg msg=CMClass.getMsg(mob,openThis,E,CMMsg.MSG_PULL,L("<S-NAME> pull(s) <T-NAME>@x1.",dir));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			if((dir.length()>0)&&(msg.tool() instanceof Room))
			{
				final Room R=(Room)msg.tool();
				dirCode=CMLib.tracking().findRoomDir(mob,R);
				if((dirCode>=0)&&(CMLib.tracking().walk(mob,dirCode,false,false,false,false)))
				{
					if(openThis instanceof Item)
						R.moveItemTo((Item)openThis,ItemPossessor.Expire.Player_Drop,ItemPossessor.Move.Followers);
					else
					if(openThis instanceof MOB)
						CMLib.tracking().walk((MOB)openThis,dirCode,((MOB)openThis).isInCombat(),false,true,true);
				}
			}
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
