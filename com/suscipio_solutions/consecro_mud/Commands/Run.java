package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings("rawtypes")
public class Run extends Go
{
	public Run(){}

	private final String[] access=I(new String[]{"RUN"});
	@Override public String[] getAccessWords(){return access;}
	public int energyExpenseFactor(){return 2;}
	@Override
	public double actionsCost(final MOB mob, final List<String> cmds)
	{
		return CMProps.getCommandActionCost(ID(), CMath.div(CMProps.getIntVar(CMProps.Int.DEFCMDTIME),400.0));
	}
	@Override
	public double combatActionsCost(MOB mob, List<String> cmds)
	{
		return CMProps.getCommandCombatActionCost(ID(), CMath.div(CMProps.getIntVar(CMProps.Int.DEFCOMCMDTIME),400.0));
	}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
	throws java.io.IOException
	{
		if(mob==null)
			return super.execute(mob, commands,metaFlags);
		final boolean wasSet = mob.isAttribute(MOB.Attrib.AUTORUN);
		mob.setAttribute(MOB.Attrib.AUTORUN,true);
		final boolean returnValue = super.execute(mob, commands,metaFlags);
		if(!wasSet)
			mob.setAttribute(MOB.Attrib.AUTORUN,false);
		return returnValue;
	}
}
