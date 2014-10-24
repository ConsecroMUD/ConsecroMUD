package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;




@SuppressWarnings("rawtypes")
public class AutoMelee extends StdCommand
{
	public AutoMelee(){}

	private final String[] access=I(new String[]{"AUTOMELEE"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(!mob.isAttribute(MOB.Attrib.AUTOMELEE))
		{
			mob.setAttribute(MOB.Attrib.AUTOMELEE,true);
			mob.tell(L("Automelee has been turned off.  You will no longer charge into melee combat from a ranged position."));
			if(mob.isMonster())
				CMLib.commands().postSay(mob,null,L("I will no longer charge into melee."),false,false);
		}
		else
		{
			mob.setAttribute(MOB.Attrib.AUTOMELEE,false);
			mob.tell(L("Automelee has been turned back on.  You will now enter melee combat normally."));
			if(mob.isMonster())
				CMLib.commands().postSay(mob,null,L("I will now enter melee combat normally."),false,false);
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
}

