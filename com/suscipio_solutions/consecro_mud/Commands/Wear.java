package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;


@SuppressWarnings({"unchecked","rawtypes"})
public class Wear extends StdCommand
{
	public Wear(){}

	private final String[] access=I(new String[]{"WEAR"});
	@Override public String[] getAccessWords(){return access;}

	public boolean wear(MOB mob, Item item, int locationIndex, boolean quiet)
	{
		String str=L("<S-NAME> put(s) on <T-NAME>.");
		int msgType=CMMsg.MSG_WEAR;
		if(item.rawProperLocationBitmap()==Wearable.WORN_HELD)
		{
			str=L("<S-NAME> hold(s) <T-NAME>.");
			msgType=CMMsg.MSG_HOLD;
		}
		else
		if((item.rawProperLocationBitmap()==Wearable.WORN_WIELD)
		||(item.rawProperLocationBitmap()==(Wearable.WORN_HELD|Wearable.WORN_WIELD)))
		{
			str=L("<S-NAME> wield(s) <T-NAME>.");
			msgType=CMMsg.MSG_WIELD;
		}
		else
		if(locationIndex!=0)
			str=L("<S-NAME> put(s) <T-NAME> on <S-HIS-HER> @x1.",Wearable.CODES.NAME(locationIndex).toLowerCase());
		final CMMsg newMsg=CMClass.getMsg(mob,item,null,msgType,quiet?null:str);
		newMsg.setValue(locationIndex);
		if(mob.location().okMessage(mob,newMsg))
		{
			mob.location().send(mob,newMsg);
			return true;
		}
		return false;
	}


	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("Wear what?"));
			return false;
		}
		final Wearable.CODES codes = Wearable.CODES.instance();
		commands.removeElementAt(0);
		if(commands.firstElement() instanceof Item)
		{
			final Item wearWhat = (Item)commands.firstElement();
			boolean quietly = false;
			int wearLocationIndex = 0;
			commands.removeElementAt(0);
			if(commands.size()>0)
			{
				if(commands.firstElement() instanceof Integer)
				{
					wearLocationIndex=((Integer)commands.firstElement()).intValue();
					commands.removeElementAt(0);
				}
				else
				if(commands.firstElement() instanceof String)
				{
					final int newDex = codes.findDex_ignoreCase((String)commands.firstElement());
					if(newDex>0)
					{
						wearLocationIndex=newDex;
						commands.removeElementAt(0);
					}
				}
				if((commands.size()>0)
				&&(commands.lastElement() instanceof String)
				&&(((String)commands.lastElement()).equalsIgnoreCase("QUIETLY")))
					quietly=true;
			}
			return wear(mob,wearWhat,wearLocationIndex,quietly);
		}

		// discover if a wear location was specified
		int wearLocationIndex=0;
		for(int i=commands.size()-2;i>0;i--)
			if(((String)commands.elementAt(i)).equalsIgnoreCase("on"))
			{
				if((i<commands.size()-2)&&((String)commands.elementAt(i+1)).equalsIgnoreCase("my"))
					commands.removeElementAt(i+1);
				final String possibleWearLocation = CMParms.combine(commands, i+1).toLowerCase().trim();
				int possIndex = CMParms.indexOfIgnoreCase(Wearable.CODES.NAMES(), possibleWearLocation);
				if(possIndex<0)
					possIndex = Wearable.CODES.FINDDEX_endsWith(" " + possibleWearLocation);
				if(possIndex>0)
				{
					wearLocationIndex=possIndex;
					while(commands.size()>i)
						commands.removeElementAt(commands.size()-1);
					break;
				}
				else
				{
					mob.tell(L("You can't wear anything on your '@x1'",possibleWearLocation));
					return false;
				}
				// will always break out here, one way or the other.
			}
		final List<Item> items=CMLib.english().fetchItemList(mob,mob,null,commands,Wearable.FILTER_UNWORNONLY,false);
		if(items.size()==0)
			mob.tell(L("You don't seem to be carrying that."));
		else
		{
			// sort hold-onlys down.
			Item I=null;
			for(int i=items.size()-2;i>=0;i--)
			{
				I=items.get(i);
				if(I.rawProperLocationBitmap()==Wearable.WORN_HELD)
				{
					items.remove(i);
					items.add(I);
				}
			}
			for(int i=0;i<items.size();i++)
			{
				I=items.get(i);
				if((items.size()==1)||(I.canWear(mob,0)))
					wear(mob,I,wearLocationIndex,false);
			}
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
