package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;


@SuppressWarnings({"unchecked","rawtypes"})
public class Remove extends StdCommand
{
	public Remove(){}

	private final String[] access=I(new String[]{"REMOVE","REM"});
	@Override public String[] getAccessWords(){return access;}



	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("Remove what?"));
			return false;
		}
		commands.removeElementAt(0);
		if(commands.firstElement() instanceof Item)
		{
			final boolean quiet=((commands.size()>1)&&(commands.lastElement() instanceof String)&&(((String)commands.lastElement()).equalsIgnoreCase("QUIETLY")));
			final Item item=(Item)commands.firstElement();
			final CMMsg newMsg=CMClass.getMsg(mob,item,null,CMMsg.MSG_REMOVE,quiet?null:L("<S-NAME> remove(s) <T-NAME>."));
			if(mob.location().okMessage(mob,newMsg))
			{
				mob.location().send(mob,newMsg);
				return true;
			}
			return false;
		}

		final List<Item> items=CMLib.english().fetchItemList(mob,mob,null,commands,Wearable.FILTER_WORNONLY,false);
		if(items.size()==0)
			mob.tell(L("You don't seem to be wearing that."));
		else
		for(int i=0;i<items.size();i++)
		{
			final Item item=items.get(i);
			final CMMsg newMsg=CMClass.getMsg(mob,item,null,CMMsg.MSG_REMOVE,L("<S-NAME> remove(s) <T-NAME>."));
			if(mob.location().okMessage(mob,newMsg))
				mob.location().send(mob,newMsg);
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
