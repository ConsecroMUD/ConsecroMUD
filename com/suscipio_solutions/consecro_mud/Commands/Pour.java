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
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;


@SuppressWarnings("rawtypes")
public class Pour extends StdCommand
{
	public Pour(){}

	private final String[] access=I(new String[]{"POUR"});
	@Override public String[] getAccessWords(){return access;}

	enum PourVerb{DEFAULT,INTO,ONTO,OUT}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("Pour what, into/onto what?"));
			return false;
		}
		commands.removeElementAt(0);
		Environmental fillFromThis=null;
		final String thingToFillFrom=(String)commands.elementAt(0);
		fillFromThis=mob.fetchItem(null,Wearable.FILTER_UNWORNONLY,thingToFillFrom);
		if((fillFromThis==null)||(!CMLib.flags().canBeSeenBy(fillFromThis,mob)))
		{
			mob.tell(L("You don't seem to have '@x1'.",thingToFillFrom));
			return false;
		}
		commands.removeElementAt(0);

		PourVerb verb=PourVerb.DEFAULT;
		if(commands.size()>1)
		{
			if((((String)commands.firstElement())).equalsIgnoreCase("into"))
				commands.removeElementAt(0);
			else
			if((((String)commands.firstElement())).equalsIgnoreCase("onto"))
			{
				commands.removeElementAt(0);
				verb=PourVerb.ONTO;
			}
			else
			if((((String)commands.firstElement())).equalsIgnoreCase("out"))
			{
				commands.removeElementAt(0);
				verb=PourVerb.OUT;
			}
		}

		Environmental fillThis;
		String msgStr;
		if(verb==PourVerb.OUT)
		{
			final Item out=CMClass.getItem("StdDrink");
			((Drink)out).setLiquidHeld(999999);
			((Drink)out).setLiquidRemaining(0);
			out.setDisplayText("");
			out.setName(L("out"));
			msgStr=L("<S-NAME> pour(s) <O-NAME> <T-NAME>.");
			mob.location().addItem(out,ItemPossessor.Expire.Resource);
			fillThis=out;
		}
		else
		{
			if(commands.size()<1)
			{
				mob.tell(L("@x1 what should I pour the @x2?",CMStrings.capitalizeAndLower(verb.name()),thingToFillFrom));
				return false;
			}
			final String thingToFill=CMParms.combine(commands,0);
			fillThis=mob.location().fetchFromMOBRoomFavorsItems(mob,null,thingToFill,Wearable.FILTER_ANY);
			if((fillThis==null)||(!CMLib.flags().canBeSeenBy(fillThis,mob)))
			{
				mob.tell(L("I don't see '@x1' here.",thingToFill));
				return false;
			}
			if((verb==PourVerb.DEFAULT)&&(!(fillThis instanceof Drink)))
				verb=PourVerb.ONTO;
			else
			if((verb==PourVerb.ONTO)&&(fillThis instanceof Drink))
				verb=PourVerb.INTO;
			if(verb==PourVerb.ONTO)
				msgStr=L("<S-NAME> pour(s) <O-NAME> onto <T-NAME>.");
			else
				msgStr=L("<S-NAME> pour(s) <O-NAME> into <T-NAME>.");
		}

		final CMMsg fillMsg=CMClass.getMsg(mob,fillThis,fillFromThis,(verb==PourVerb.ONTO)?CMMsg.MSG_POUR:CMMsg.MSG_FILL,msgStr);
		if(mob.location().okMessage(mob,fillMsg))
			mob.location().send(mob,fillMsg);

		if(verb==PourVerb.OUT)
			fillThis.destroy();
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
