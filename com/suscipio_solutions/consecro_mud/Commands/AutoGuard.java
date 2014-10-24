package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;




@SuppressWarnings("rawtypes")
public class AutoGuard extends StdCommand
{
	public AutoGuard(){}

	private final String[] access=I(new String[]{"AUTOGUARD","GUARD"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if((!mob.isAttribute(MOB.Attrib.AUTOGUARD))
		   ||((commands.size()>0)&&(((String)commands.firstElement()).toUpperCase().startsWith("G"))))
		{
			mob.setAttribute(MOB.Attrib.AUTOGUARD,true);
			mob.tell(L("You are now on guard. You will no longer follow group leaders."));
			if(mob.isMonster())
				CMLib.commands().postSay(mob,null,L("I am now on guard."),false,false);
		}
		else
		{
			mob.setAttribute(MOB.Attrib.AUTOGUARD,false);
			mob.tell(L("You are no longer on guard.  You will now follow group leaders."));
			if(mob.isMonster())
				CMLib.commands().postSay(mob,null,L("I will now follow my group leader."),false,false);
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}

