package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;


@SuppressWarnings("rawtypes")
public class Boot extends StdCommand
{
	public Boot(){}

	private final String[] access=I(new String[]{"BOOT"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		commands.removeElementAt(0);
		if(mob.session()==null) return false;
		if(commands.size()==0)
		{
			mob.tell(L("Boot out who?"));
			return false;
		}
		final String whom=CMParms.combine(commands,0);
		boolean boot=false;
		for(final Session S : CMLib.sessions().allIterable())
		{
			if(((S.mob()!=null)&&(CMLib.english().containsString(S.mob().name(),whom)))
			||(S.getAddress().equalsIgnoreCase(whom)))
			{
				if(S==mob.session())
				{
					mob.tell(L("Try QUIT."));
					return false;
				}
				if(S.mob()!=null)
				{
					mob.tell(L("You boot @x1",S.mob().name()));
					if(S.mob().location()!=null)
						S.mob().location().show(S.mob(),null,CMMsg.MSG_OK_VISUAL,L("Something is happening to <S-NAME>."));
				}
				else
					mob.tell(L("You boot @x1",S.getAddress()));
				S.stopSession(false,false,false);
				if(((S.getPreviousCMD()==null)||(S.getPreviousCMD().size()==0))
				&&(!CMLib.flags().isInTheGame(S.mob(),true)))
					CMLib.sessions().stopSessionAtAllCosts(S);
				boot=true;
				break;
			}
		}
		if(!boot)
			mob.tell(L("You can't find anyone by that name or ip address."));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.BOOT);}


}
