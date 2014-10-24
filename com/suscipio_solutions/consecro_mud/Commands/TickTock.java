package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.CMLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings("rawtypes")
public class TickTock extends StdCommand
{
	public TickTock(){}

	private final String[] access=I(new String[]{"TICKTOCK"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final String s=CMParms.combine(commands,1).toLowerCase();
		try
		{
			if(CMath.isInteger(s))
			{
				int h=CMath.s_int(s);
				if(h==0) h=1;
				mob.tell(L("..tick..tock.."));
				mob.location().getArea().getTimeObj().tickTock(h);
				mob.location().getArea().getTimeObj().save();
			}
			else
			if(s.startsWith("clantick"))
				CMLib.clans().tickAllClans();
			else
			{
				for(final Enumeration e=CMLib.libraries();e.hasMoreElements();)
				{
					final CMLibrary lib=(CMLibrary)e.nextElement();
					if((lib.getServiceClient()!=null)&&(s.equalsIgnoreCase(lib.getServiceClient().getName())))
					{
						if(lib instanceof Runnable)
							((Runnable)lib).run();
						else
							lib.getServiceClient().tickTicker(true);
						mob.tell(L("Done."));
						return false;
					}
				}
				mob.tell(L("Ticktock what?  Enter a number of mud-hours, or clanticks, or thread id."));
			}
		}
		catch(final Exception e)
		{
			mob.tell(L("Ticktock failed: @x1",e.getMessage()));
		}

		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.TICKTOCK);}


}
