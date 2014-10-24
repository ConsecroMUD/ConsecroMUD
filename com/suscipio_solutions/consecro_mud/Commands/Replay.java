package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings("rawtypes")
public class Replay extends StdCommand
{
	public Replay(){}

	private final String[] access=I(new String[]{"REPLAY"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(!mob.isMonster())
		{
			final Session S=mob.session();
			int num=Session.MAX_PREVMSGS;
			if(commands.size()>1) num=CMath.s_int(CMParms.combine(commands,1));
			if(num<=0) return false;
			final java.util.List<String> last=S.getLastMsgs();
			if(num>last.size()) num=last.size();
			for(int v=last.size()-num;v<last.size();v++)
				S.onlyPrint((last.get(v))+"\n\r",true);
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
