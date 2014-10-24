package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Deity;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;


@SuppressWarnings("rawtypes")
public class Serve extends StdCommand
{
	public Serve(){}

	private final String[] access=I(new String[]{"SERVE"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("Serve whom?"));
			return false;
		}
		commands.removeElementAt(0);
		final MOB recipient=mob.location().fetchInhabitant(CMParms.combine(commands,0));
		if((recipient!=null)&&(recipient.isMonster())&&(!(recipient instanceof Deity)))
		{
			mob.tell(L("You may not serve @x1.",recipient.name()));
			return false;
		}
		if((recipient==null)||(!CMLib.flags().canBeSeenBy(recipient,mob)))
		{
			mob.tell(L("I don't see @x1 here.",CMParms.combine(commands,0)));
			return false;
		}
		final CMMsg msg=CMClass.getMsg(mob,recipient,null,CMMsg.MSG_SERVE,L("<S-NAME> swear(s) fealty to <T-NAMESELF>."));
		if(mob.location().okMessage(mob,msg))
			mob.location().send(mob,msg);
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return false;}


}
