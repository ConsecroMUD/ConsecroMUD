package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;


@SuppressWarnings("rawtypes")
public class WizEmote extends StdCommand
{
	public WizEmote(){}

	private final String[] access=I(new String[]{"WIZEMOTE"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()>2)
		{
			final String who=(String)commands.elementAt(1);
			final String msg=CMParms.combineQuoted(commands,2);
			Room R=CMLib.map().getRoom(who);
			if(who.toUpperCase().equals("HERE")) R=mob.location();
			final Area A=CMLib.map().findAreaStartsWith(who);
			final Clan C=CMLib.clans().findClan(who);
			if(who.toUpperCase().equals("ALL"))
			{
				for(final Session S : CMLib.sessions().localOnlineIterable())
				{
					if((S.mob()!=null)
					&&(S.mob().location()!=null)
					&&(CMSecurity.isAllowed(mob,S.mob().location(),CMSecurity.SecFlag.WIZEMOTE)))
	  					S.stdPrintln("^w"+msg+"^?");
				}
			}
			else
			if(R!=null)
			{
				for(final Session S : CMLib.sessions().localOnlineIterable())
				{
					if((S.mob()!=null)
					&&(S.mob().location()==R)
					&&(CMSecurity.isAllowed(mob,R,CMSecurity.SecFlag.WIZEMOTE)))
						S.stdPrintln("^w"+msg+"^?");
				}
			}
			else
			if(A!=null)
			{
				for(final Session S : CMLib.sessions().localOnlineIterable())
				{
					if((S.mob()!=null)
					&&(S.mob().location()!=null)
					&&(A.inMyMetroArea(S.mob().location().getArea()))
					&&(CMSecurity.isAllowed(mob,S.mob().location(),CMSecurity.SecFlag.WIZEMOTE)))
						S.stdPrintln("^w"+msg+"^?");
				}
			}
			else
			if(C!=null)
			{
				for(final Session S : CMLib.sessions().localOnlineIterable())
				{
					if((S.mob()!=null)
					&&(S.mob().getClanRole(C.clanID())!=null)
					&&(CMSecurity.isAllowed(mob,S.mob().location(),CMSecurity.SecFlag.WIZEMOTE)))
						S.stdPrintln("^w"+msg+"^?");
				}
			}
			else
			{
				boolean found=false;
				for(final Session S : CMLib.sessions().localOnlineIterable())
				{
					if((S.mob()!=null)
					&&(S.mob().location()!=null)
					&&(CMSecurity.isAllowed(mob,S.mob().location(),CMSecurity.SecFlag.WIZEMOTE))
					&&(CMLib.english().containsString(S.mob().name(),who)
						||CMLib.english().containsString(S.mob().location().getArea().name(),who)))
					{
	  					S.stdPrintln("^w"+msg+"^?");
						found=true;
						break;
					}
				}
				if(!found)
					mob.tell(L("You can't find anyone or anywhere by that name."));
			}
		}
		else
			mob.tell(L("You must specify either all, or an area/mob name, and an message."));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.WIZEMOTE);}


}
