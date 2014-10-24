package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings("rawtypes")
public class Wimpy extends StdCommand
{
	public Wimpy(){}

	private final String[] access=I(new String[]{"WIMPY"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("Change your wimp level to what?"));
			return false;
		}
		final String amt=CMParms.combine(commands,1);
		int newWimp = mob.baseState().getHitPoints();
		if(CMath.isPct(amt))
			newWimp = (int)Math.round(CMath.s_pct(amt) * newWimp);
		else
		if(CMath.isInteger(amt))
			newWimp=CMath.s_int(amt);
		else
		{
			mob.tell(L("You can't change your wimp level to '@x1'",amt));
			return false;
		}
		mob.setWimpHitPoint(newWimp);
		mob.tell(L("Your wimp level has been changed to @x1 hit points.",""+mob.getWimpHitPoint()));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
