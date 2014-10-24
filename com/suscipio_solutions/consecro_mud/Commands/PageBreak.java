package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings("rawtypes")
public class PageBreak extends StdCommand
{
	public PageBreak(){}

	private final String[] access=I(new String[]{"PAGEBREAK"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if((mob==null)||(mob.playerStats()==null))
			return false;

		if(commands.size()<2)
		{
			final String pageBreak=(mob.playerStats().getPageBreak()!=0)?(""+mob.playerStats().getPageBreak()):"Disabled";
			mob.tell(L("Change your page break to what? Your current page break setting is: @x1. Enter a number larger than 0 or 'disable'.",pageBreak));
			return false;
		}
		final String newBreak=CMParms.combine(commands,1);
		int newVal=mob.playerStats().getWrap();
		if((CMath.isInteger(newBreak))&&(CMath.s_int(newBreak)>0))
			newVal=CMath.s_int(newBreak);
		else
		if("DISABLED".startsWith(newBreak.toUpperCase()))
			newVal=0;
		else
		{
			mob.tell(L("'@x1' is not a valid setting. Enter a number larger than 0 or 'disable'.",newBreak));
			return false;
		}
		mob.playerStats().setPageBreak(newVal);
		final String pageBreak=(mob.playerStats().getPageBreak()!=0)?(""+mob.playerStats().getPageBreak()):"Disabled";
		mob.tell(L("Your new page break setting is: @x1.",pageBreak));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}

