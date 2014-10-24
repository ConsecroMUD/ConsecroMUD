package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;


@SuppressWarnings({"unchecked","rawtypes"})
public class Mood extends StdCommand
{
	public Mood(){}

	private final String[] access=I(new String[]{"MOOD"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final Ability A=CMClass.getAbility("Mood");
		if(A!=null)
		{
			final Vector V=new XVector(commands);
			V.removeElementAt(0);
			A.invoke(mob,V,mob,true,0);
		}
		else
			mob.tell(L("This command is not implemented."));
		return false;
	}
	@Override public boolean canBeOrdered(){return true;}
}
