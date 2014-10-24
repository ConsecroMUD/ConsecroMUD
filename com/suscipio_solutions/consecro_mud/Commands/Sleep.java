package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


@SuppressWarnings("rawtypes")
public class Sleep extends StdCommand
{
	public Sleep(){}

	private final String[] access=I(new String[]{"SLEEP","SL"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(CMLib.flags().isSleeping(mob))
		{
			mob.tell(L("You are already asleep!"));
			return false;
		}
		final Room R=mob.location();
		if(R==null)
			return false;
		if(commands.size()<=1)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,null,CMMsg.MSG_SLEEP,L("<S-NAME> lay(s) down and take(s) a nap."));
			if(R.okMessage(mob,msg))
				R.send(mob,msg);
			return false;
		}
		final String possibleRideable=CMParms.combine(commands,1);
		final Environmental E=R.fetchFromRoomFavorItems(null,possibleRideable);
		if((E==null)||(!CMLib.flags().canBeSeenBy(E,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",possibleRideable));
			return false;
		}
		String mountStr=null;
		if(E instanceof Rideable)
			mountStr="<S-NAME> "+((Rideable)E).mountString(CMMsg.TYP_SLEEP,mob)+" <T-NAME>.";
		else
			mountStr=L("<S-NAME> sleep(s) on <T-NAME>.");
		String sourceMountStr=null;
		if(!CMLib.flags().canBeSeenBy(E,mob))
			sourceMountStr=mountStr;
		else
		{
			sourceMountStr=CMStrings.replaceAll(mountStr,"<T-NAME>",E.name());
			sourceMountStr=CMStrings.replaceAll(sourceMountStr,"<T-NAMESELF>",E.name());
		}
		final CMMsg msg=CMClass.getMsg(mob,E,null,CMMsg.MSG_SLEEP,sourceMountStr,mountStr,mountStr);
		if(R.okMessage(mob,msg))
			R.send(mob,msg);
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
