package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Electronics;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Software;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings("rawtypes")
public class Deactivate extends StdCommand
{
	public Deactivate(){}

	private final String[] access=I(new String[]{"DEACTIVATE","DEACT","DEA","<"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final Room R=mob.location();
		if((commands.size()<2)||(R==null))
		{
			mob.tell(L("Deactivate what?"));
			return false;
		}
		commands.removeElementAt(0);
		final String what=(String)commands.lastElement();
		final String whole=CMParms.combine(commands,0);
		Item item=null;
		Environmental E=mob.location().fetchFromMOBRoomFavorsItems(mob,null,whole,Wearable.FILTER_ANY);
		if((!(E instanceof Electronics))||(E instanceof Software))
			E=null;
		if(E==null)
			for(int i=0;i<R.numItems();i++)
			{
				final Item I=R.getItem(i);
				if((I instanceof Electronics.ElecPanel)
				&&(((Electronics.ElecPanel)I).isOpen()))
				{
					E=R.fetchFromRoomFavorItems(I, whole);
					if((E instanceof Electronics)&&(!(E instanceof Software)))
						break;
				}
			}
		if((!(E instanceof Electronics))||(E instanceof Software))
			E=null;
		else
		{
			item=(Item)E;
			commands.clear();
		}
		if(E==null)
		{
			E=mob.location().fetchFromMOBRoomFavorsItems(mob,null,what,Wearable.FILTER_ANY);
			if((!(E instanceof Electronics))||(E instanceof Software))
				E=null;
			if(E==null)
				for(int i=0;i<R.numItems();i++)
				{
					final Item I=R.getItem(i);
					if((I instanceof Electronics.ElecPanel)
					&&(((Electronics.ElecPanel)I).isOpen()))
					{
						E=R.fetchFromRoomFavorItems(I, what);
						if((E instanceof Electronics)&&(!(E instanceof Software)))
							break;
					}
				}
			if((!(E instanceof Electronics))||(E instanceof Software))
				E=null;
			if((E==null)&&(mob.riding() instanceof Electronics.Computer))
			{
				E=mob.riding();
				item=(Item)E;
			}
			else
			{
				commands.removeElementAt(commands.size()-1);
				item=(Item)E;
			}
		}
		if(E==null)
		{
			mob.tell(L("You don't see anything called '@x1' or '@x2' here that you can deactivate.",what,whole));
			return false;
		}
		else
		if(item==null)
		{
			mob.tell(L("You can't deactivate '@x1'.",E.name()));
		}

		final String rest=CMParms.combine(commands,0);
		final CMMsg newMsg=CMClass.getMsg(mob,item,null,CMMsg.MSG_DEACTIVATE,null,CMMsg.MSG_DEACTIVATE,(rest.length()==0)?null:rest,CMMsg.MSG_DEACTIVATE,null);
		if(mob.location().okMessage(mob,newMsg))
			mob.location().send(mob,newMsg);
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}
}
