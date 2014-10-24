package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Resources;


@SuppressWarnings("rawtypes")
public class NoPurge extends StdCommand
{
	public NoPurge(){}

	private final String[] access=I(new String[]{"NOPURGE"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		commands.removeElementAt(0);
		final String protectMe=CMParms.combine(commands,0);
		if(protectMe.length()==0)
		{
			mob.tell(L("Protect whom?  Enter a player name to protect from autopurge."));
			return false;
		}
		if((!CMLib.players().playerExists(protectMe))&&(!CMLib.players().accountExists(protectMe))&&(CMLib.clans().getClan(protectMe)==null))
		{
			mob.tell(L("Protect whom?  '@x1' is not a known player.",protectMe));
			return false;
		}
		final List<String> protectedOnes=Resources.getFileLineVector(Resources.getFileResource("protectedplayers.ini",false));
		if((protectedOnes!=null)&&(protectedOnes.size()>0))
		for(int b=0;b<protectedOnes.size();b++)
		{
			final String B=protectedOnes.get(b);
			if(B.equalsIgnoreCase(protectMe))
			{
				mob.tell(L("That player already protected.  Do LIST NOPURGE and check out #@x1.",""+(b+1)));
				return false;
			}
		}
		mob.tell(L("The player '@x1' is now protected from autopurge.",protectMe));
		final StringBuffer str=Resources.getFileResource("protectedplayers.ini",false);
		if(protectMe.trim().length()>0) str.append(protectMe+"\n");
		Resources.updateFileResource("::protectedplayers.ini",str);
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.NOPURGE);}


}
