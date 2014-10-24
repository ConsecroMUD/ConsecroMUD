package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMStrings;


@SuppressWarnings("rawtypes")
public class Config extends StdCommand
{
	public Config(){}

	private final String[] access=I(new String[]{"CONFIG","AUTO"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final StringBuffer msg=new StringBuffer(L("^HYour configuration flags:^?\n\r"));
		for(MOB.Attrib a : MOB.Attrib.values())
		{
			if((a==MOB.Attrib.SYSOPMSGS)&&(!(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.SYSMSGS))))
				continue;
			if((a==MOB.Attrib.AUTOMAP)&&(CMProps.getIntVar(CMProps.Int.AWARERANGE)<=0))
				continue;

			msg.append(CMStrings.padRight(a.getName(),15)+": ");
			boolean set=mob.isAttribute(a);
			if(a.isAutoReversed()) 
				set=!set;
			msg.append(set?L("ON"):L("OFF"));
			msg.append("\n\r");
		}
		if(mob.playerStats()!=null)
		{
			final String wrap=(mob.playerStats().getWrap()!=0)?(""+mob.playerStats().getWrap()):"Disabled";
			msg.append(CMStrings.padRight(L("LINEWRAP"),15)+": "+wrap);
			msg.append("\n\r");
			final String pageBreak=(mob.playerStats().getPageBreak()!=0)?(""+mob.playerStats().getPageBreak()):"Disabled";
			msg.append(CMStrings.padRight(L("PAGEBREAK"),15)+": "+pageBreak);
			msg.append("\n\r");
		}
		mob.tell(msg.toString());
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
