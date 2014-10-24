package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings("rawtypes")
public class LineWrap extends StdCommand
{
	public LineWrap(){}

	private final String[] access=I(new String[]{"LINEWRAP"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if((mob==null)||(mob.playerStats()==null))
			return false;

		if(commands.size()<2)
		{
			final String wrap=(mob.playerStats().getWrap()!=0)?(""+mob.playerStats().getWrap()):"Disabled";
			mob.tell(L("Change your line wrap to what? Your current line wrap setting is: @x1. Enter a number larger than 10 or 'disable'.",wrap));
			return false;
		}
		final String newWrap=CMParms.combine(commands,1);
		int newVal=mob.playerStats().getWrap();
		if((CMath.isInteger(newWrap))&&(CMath.s_int(newWrap)>10))
			newVal=CMath.s_int(newWrap);
		else
		if("DISABLED".startsWith(newWrap.toUpperCase()))
			newVal=0;
		else
		{
			mob.tell(L("'@x1' is not a valid setting. Enter a number larger than 10 or 'disable'.",newWrap));
			return false;
		}
		mob.playerStats().setWrap(newVal);
		final String wrap=(mob.playerStats().getWrap()!=0)?(""+mob.playerStats().getWrap()):"Disabled";
		mob.tell(L("Your new line wrap setting is: @x1.",wrap));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}

