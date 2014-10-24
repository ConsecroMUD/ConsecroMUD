package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;


@SuppressWarnings("rawtypes")
public class Exits extends StdCommand
{
	public Exits(){}

	private final String[] access=I(new String[]{"EXITS","EX"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final Room R=mob.location();
		if(R!=null)
		{
			final CMMsg exitMsg=CMClass.getMsg(mob,R,null,CMMsg.MSG_LOOK_EXITS,null);
			if((commands!=null)&&(commands.size()>1)&&(commands.lastElement() instanceof String)&&(((String)commands.lastElement()).equalsIgnoreCase("SHORT")))
				exitMsg.setValue(CMMsg.MASK_OPTIMIZE);
			if(R.okMessage(mob, exitMsg))
				R.send(mob, exitMsg);
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
