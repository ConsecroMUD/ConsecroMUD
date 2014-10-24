package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings("rawtypes")
public class OutFit extends StdCommand
{
	public OutFit(){}

	private final String[] access=I(new String[]{"OUTFIT"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean preExecute(MOB mob, Vector commands, int metaFlags, int secondsElapsed, double actionsRemaining)
	throws java.io.IOException
	{
		if(secondsElapsed>8.0)
			mob.tell(L("You feel your outfit plea is almost answered."));
		else
		if(secondsElapsed>4.0)
			mob.tell(L("Your plea swirls around you."));
		else
		if(actionsRemaining>0.0)
			mob.tell(L("You invoke a plea for mystical outfitting and await the answer."));
		return true;
	}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob==null) return false;
		if(mob.charStats()==null) return false;
		final CharClass C=mob.charStats().getCurrentClass();
		final Race R=mob.charStats().getMyRace();
		if(C!=null)
			CMLib.utensils().outfit(mob,C.outfit(mob));
		if(R!=null)
			CMLib.utensils().outfit(mob,R.outfit(mob));
		mob.tell(L("\n\r"));
		final Command C2=CMClass.getCommand("Equipment");
		if(C2!=null) C2.executeInternal(mob, metaFlags);
		mob.tell(L("\n\rUseful equipment appears mysteriously out of the Java Plane."));
		mob.recoverCharStats();
		mob.recoverMaxState();
		mob.recoverPhyStats();
		return false;
	}
	@Override
	public double combatActionsCost(final MOB mob, final List<String> cmds)
	{
		return CMProps.getCommandCombatActionCost(ID(),CMath.div(CMProps.getIntVar(CMProps.Int.DEFCOMCMDTIME),25.0));
	}
	@Override
	public double actionsCost(MOB mob, List<String> cmds)
	{
		return CMProps.getCommandActionCost(ID(),CMath.div(CMProps.getIntVar(CMProps.Int.DEFCMDTIME),25.0));
	}
	@Override public boolean canBeOrdered(){return false;}


}
