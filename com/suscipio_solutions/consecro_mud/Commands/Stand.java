package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;


@SuppressWarnings("rawtypes")
public class Stand extends StdCommand
{
	public Stand(){}

	private final String[] access=I(new String[]{"STAND","ST","STA","STAN"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final boolean ifnecessary=((commands.size()>1)&&(((String)commands.lastElement()).equalsIgnoreCase("IFNECESSARY")));
		final Room room = CMLib.map().roomLocation(mob);
		if(CMLib.flags().isStanding(mob))
		{
			if(!ifnecessary)
				mob.tell(L("You are already standing!"));
		}
		else
		if((mob.session()!=null)&&(mob.session().isStopped()))
			mob.tell(L("You may not stand up."));
		else
		if(room!=null)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,null,CMMsg.MSG_STAND,mob.amDead()?null:L("<S-NAME> stand(s) up."));
			if(room.okMessage(mob,msg))
				room.send(mob,msg);
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
