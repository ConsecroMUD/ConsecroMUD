package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Directions;


@SuppressWarnings("rawtypes")
public class Crawl extends Go
{
	public Crawl(){}

	private final String[] access=I(new String[]{"CRAWL","CR"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean preExecute(MOB mob, Vector commands, int metaFlags, int secondsElapsed, double actionsRemaining)
		throws java.io.IOException
	{
		if(secondsElapsed==0)
		{
			final int direction=Directions.getGoodDirectionCode(CMParms.combine(commands,1));
			if(direction<0)
			{
				mob.tell(L("Crawl which way?\n\rTry north, south, east, west, up, or down."));
				return false;
			}
		}
		return true;
	}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final int direction=Directions.getGoodDirectionCode(CMParms.combine(commands,1));
		if(direction>=0)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,null,CMMsg.MSG_SIT,null);
			if(CMLib.flags().isSitting(mob)||(mob.location().okMessage(mob,msg)))
			{
				if(!CMLib.flags().isSitting(mob))
					mob.location().send(mob,msg);
				CMLib.tracking().walk(mob,direction,false,false,false);
			}
		}
		else
		{
			mob.tell(L("Crawl which way?\n\rTry north, south, east, west, up, or down."));
			return false;
		}
		return false;
	}
	@Override
	public double actionsCost(final MOB mob, final List<String> cmds)
	{
		return CMProps.getCommandActionCost(ID(), CMath.greater(CMath.div(CMProps.getIntVar(CMProps.Int.DEFCMDTIME),50.0),1.0));
	}
	@Override
	public double combatActionsCost(MOB mob, List<String> cmds)
	{
		return CMProps.getCommandCombatActionCost(ID(), CMath.greater(CMath.div(CMProps.getIntVar(CMProps.Int.DEFCOMCMDTIME),50.0),2.0));
	}
	@Override public boolean canBeOrdered(){return true;}


}
