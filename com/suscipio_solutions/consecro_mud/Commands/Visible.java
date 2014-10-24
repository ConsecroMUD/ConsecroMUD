package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Visible extends StdCommand
{
	public Visible(){}

	private final String[] access=I(new String[]{"VISIBLE","VIS"});
	@Override public String[] getAccessWords(){return access;}

	public static java.util.List<Ability> returnOffensiveAffects(Physical fromMe)
	{
		final MOB newMOB=CMClass.getFactoryMOB();
		final Vector offenders=new Vector();
		for(int a=0;a<fromMe.numEffects();a++) // personal
		{
			final Ability A=fromMe.fetchEffect(a);
			if((A!=null)&&(A.canBeUninvoked()))
			{
				try
				{
					newMOB.recoverPhyStats();
					A.affectPhyStats(newMOB,newMOB.phyStats());
					if(CMLib.flags().isInvisible(newMOB)||CMLib.flags().isHidden(newMOB))
					  offenders.addElement(A);
				}
				catch(final Exception e)
				{}
			}
		}
		newMOB.destroy();
		return offenders;
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final String str=L("Prop_WizInvis");
		final Ability A=mob.fetchEffect(str);
		boolean didSomething=false;
		if(A!=null)
		{
			final Command C=CMClass.getCommand("WizInv");
			if((C!=null)&&(C.securityCheck(mob)))
			{
				didSomething=true;
				C.execute(mob,new XVector("WIZINV","OFF"),metaFlags);
			}
		}
		final java.util.List V=returnOffensiveAffects(mob);
		if(V.size()==0)
		{
			if(!didSomething)
			mob.tell(L("You are not invisible or hidden!"));
		}
		else
		for(int v=0;v<V.size();v++)
			((Ability)V.get(v)).unInvoke();
		return false;
	}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public double combatActionsCost(MOB mob, List<String> cmds){return 0.25;}
	@Override public boolean canBeOrdered(){return true;}

}
