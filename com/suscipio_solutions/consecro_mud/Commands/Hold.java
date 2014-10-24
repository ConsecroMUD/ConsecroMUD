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
public class Hold extends StdCommand
{
	public Hold(){}

	private final String[] access=I(new String[]{"HOLD","HOL","HO","H"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("Hold what?"));
			return false;
		}
		commands.removeElementAt(0);
		final List<Item> items=CMLib.english().fetchItemList(mob,mob,null,commands,Wearable.FILTER_UNWORNONLY,false);
		if(items.size()==0)
			mob.tell(L("You don't seem to be carrying that."));
		else
		for(int i=0;i<items.size();i++)
			if((items.size()==1)||(items.get(i).canWear(mob,Wearable.WORN_HELD)))
			{
				final Item item=items.get(i);
				int msgType=CMMsg.MSG_HOLD;
				String str=L("<S-NAME> hold(s) <T-NAME>.");
				if((mob.freeWearPositions(Wearable.WORN_WIELD,(short)0,(short)0)>0)
				&&((item.rawProperLocationBitmap()==Wearable.WORN_WIELD)
				||(item.rawProperLocationBitmap()==(Wearable.WORN_HELD|Wearable.WORN_WIELD))))
				{
					str=L("<S-NAME> wield(s) <T-NAME>.");
					msgType=CMMsg.MSG_WIELD;
				}
				final CMMsg newMsg=CMClass.getMsg(mob,item,null,msgType,str);
				if(mob.location().okMessage(mob,newMsg))
					mob.location().send(mob,newMsg);
			}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
