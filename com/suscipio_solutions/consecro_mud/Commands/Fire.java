package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings("rawtypes")
public class Fire extends StdCommand
{
	public Fire(){}

	private final String[] access=I(new String[]{"FIRE"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		String rest="ALL";
		if(commands.size()>1) rest=CMParms.combine(commands,1);

		Environmental target=mob.location().fetchFromRoomFavorMOBs(null,rest);
		if((target!=null)&&(!target.name().equalsIgnoreCase(rest))&&(rest.length()<4))
		   target=null;
		if((target!=null)&&(!CMLib.flags().canBeSeenBy(target,mob)))
			target=null;
		if(target==null)
			mob.tell(L("Fire whom?"));
		else
		{
			final CMMsg msg=CMClass.getMsg(mob,target,null,CMMsg.MSG_SPEAK,L("^T<S-NAME> say(s) to <T-NAMESELF> 'You are fired!'^?"));
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
