package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


@SuppressWarnings("rawtypes")
public class Sit extends StdCommand
{
	public Sit(){}

	private final String[] access=I(new String[]{"SIT","REST","R"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(CMLib.flags().isSitting(mob))
		{
			mob.tell(L("You are already sitting!"));
			return false;
		}
		if(commands.size()<=1)
		{
			CMMsg msg;
			if(CMLib.flags().isSleeping(mob))
				msg=CMClass.getMsg(mob,null,null,CMMsg.MSG_SIT,L("<S-NAME> awake(s) and sit(s) up."));
			else
				msg=CMClass.getMsg(mob,null,null,CMMsg.MSG_SIT,L("<S-NAME> sit(s) down and take(s) a rest."));
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);
			return false;
		}
		final String possibleRideable=CMParms.combine(commands,1);
		Environmental E=null;
		if(possibleRideable.length()>0)
		{
			E=mob.location().fetchFromRoomFavorItems(null,possibleRideable);
			if((E==null)||(!CMLib.flags().canBeSeenBy(E,mob)))
			{
				mob.tell(L("You don't see '@x1' here.",possibleRideable));
				return false;
			}
			if(E instanceof MOB)
			{
				final Command C=CMClass.getCommand("Mount");
				if(C!=null) return C.execute(mob,commands,metaFlags);
			}
		}
		String mountStr=null;
		if(E instanceof Rideable)
			mountStr=L("<S-NAME> "+((Rideable)E).mountString(CMMsg.TYP_SIT,mob)+" <T-NAME>.");
		else
			mountStr=L("<S-NAME> sit(s) on <T-NAME>.");
		final CMMsg msg=CMClass.getMsg(mob,E,null,CMMsg.MSG_SIT,mountStr);
		if(mob.location().okMessage(mob,msg))
			mob.location().send(mob,msg);
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
