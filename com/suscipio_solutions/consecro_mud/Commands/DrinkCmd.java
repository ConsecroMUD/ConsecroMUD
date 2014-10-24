package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings("rawtypes")
public class DrinkCmd extends StdCommand
{
	public DrinkCmd(){}

	private final String[] access=I(new String[]{"DRINK","DR","DRI"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if((commands.size()<2)&&(!(mob.location() instanceof Drink)))
		{
			mob.tell(L("Drink what?"));
			return false;
		}
		commands.removeElementAt(0);
		if((commands.size()>1)&&(((String)commands.firstElement()).equalsIgnoreCase("from")))
			commands.removeElementAt(0);

		Environmental thisThang=null;
		if((commands.size()==0)&&(mob.location() instanceof Drink))
			thisThang=mob.location();
		else
		{
			thisThang=mob.location().fetchFromMOBRoomFavorsItems(mob,null,CMParms.combine(commands,0),Wearable.FILTER_ANY);
			if((thisThang==null)
			||((!mob.isMine(thisThang))
			   &&(!CMLib.flags().canBeSeenBy(thisThang,mob))))
			{
				mob.tell(L("You don't see '@x1' here.",CMParms.combine(commands,0)));
				return false;
			}
		}
		String str=L("<S-NAME> take(s) a drink from <T-NAMESELF>.");
		Environmental tool=null;
		if((thisThang instanceof Drink)
		&&(((Drink)thisThang).liquidRemaining()>0)
		&&(((Drink)thisThang).liquidType()!=RawMaterial.RESOURCE_FRESHWATER))
			str=L("<S-NAME> take(s) a drink of @x1 from <T-NAMESELF>.",RawMaterial.CODES.NAME(((Drink)thisThang).liquidType()).toLowerCase());
		else
		if(thisThang instanceof Container)
		{
			final List<Item> V=((Container)thisThang).getContents();
			for(int v=0;v<V.size();v++)
			{
				final Item I=V.get(v);
				if((I instanceof Drink)&&(I instanceof RawMaterial))
				{
					tool=thisThang;
					thisThang=I;
					str=L("<S-NAME> take(s) a drink of <T-NAMESELF> from <O-NAMESELF>.");
					break;
				}
			}
		}
		final CMMsg newMsg=CMClass.getMsg(mob,thisThang,tool,CMMsg.MSG_DRINK,str+CMLib.protocol().msp("drink.wav",10));
		if(mob.location().okMessage(mob,newMsg))
			mob.location().send(mob,newMsg);
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
