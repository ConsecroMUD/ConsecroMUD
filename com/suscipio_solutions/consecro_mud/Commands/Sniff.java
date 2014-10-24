package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings("rawtypes")
public class Sniff extends StdCommand
{
	public Sniff(){}

	private final String[] access=I(new String[]{"SNIFF","SMELL"});
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
		final String textMsg="<S-NAME> sniff(s)";
		if(mob.location()==null)
			return false;

		if((commands!=null)&&(commands.size()>1))
		{
			Environmental thisThang=null;

			final String ID=CMParms.combine(commands,1);
			if(ID.equalsIgnoreCase("SELF")||ID.equalsIgnoreCase("ME"))
				thisThang=mob;

			if(thisThang==null)
				thisThang=mob.location().fetchFromMOBRoomFavorsItems(mob,null,ID,Wearable.FILTER_ANY);
			if(thisThang!=null)
			{
				String name=" <T-NAMESELF>";
 				if(thisThang instanceof Room)
				{
					if(thisThang==mob.location())
						name=" around";
				}
				final CMMsg msg=CMClass.getMsg(mob,thisThang,null,CMMsg.MSG_SNIFF,textMsg+name+".");
				if(mob.location().okMessage(mob,msg))
					mob.location().send(mob,msg);
			}
			else
				mob.tell(L("You don't see that here!"));
		}
		else
		{
			if((commands!=null)&&(commands.size()>0))
				if(((String)commands.elementAt(0)).toUpperCase().startsWith("E"))
				{
					mob.tell(L("Sniff what?"));
					return false;
				}

			final CMMsg msg=CMClass.getMsg(mob,mob.location(),null,CMMsg.MSG_SNIFF,(quiet?null:textMsg+" around."),CMMsg.MSG_SNIFF,(quiet?null:textMsg+" you."),CMMsg.MSG_SNIFF,(quiet?null:textMsg+" around."));
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);
		}
		return false;
	}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public double combatActionsCost(MOB mob, List<String> cmds){return 0.25;}
	@Override public boolean canBeOrdered(){return true;}


}
