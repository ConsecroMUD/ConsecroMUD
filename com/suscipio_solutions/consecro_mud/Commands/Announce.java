package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;

@SuppressWarnings("rawtypes")
public class Announce extends StdCommand
{
	public Announce(){}

	private final String[] access=I(new String[]{"ANNOUNCE","ANNOUNCETO","ANNOUNCEMSG"});
	@Override public String[] getAccessWords(){return access;}

	public void sendAnnounce(MOB from, String announcement, Session S)
	{
	  	final StringBuffer Message=new StringBuffer("");
		if((from!=null)&&(from.playerStats()!=null)&&(from.playerStats().getAnnounceMessage().length()>0))
			Message.append(from.playerStats().getAnnounceMessage()+" '"+announcement+"'.^.^N");
		else
		{
		  	int alignType=2;
			if (CMLib.flags().isEvil(S.mob()))
				alignType = 0;
			else
			if (CMLib.flags().isGood(S.mob()))
				alignType = 1;
		  	switch(alignType)
		  	{
		  	  case 0:
		  		Message.append("^rA terrifying voice bellows out of Hell '"+announcement+"'.^N");
		  		break;
		  	  case 1:
		  		Message.append("^wAn awe-inspiring voice thunders down from Heaven '"+announcement+"'.^N");
		  		break;
		  	  case 2:
		  		Message.append("^pA powerful voice rings out '"+announcement+"'.^N");
		  		break;
		  	}
		}
	  	S.stdPrintln(Message.toString());
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{

		String cmd=((String)commands.firstElement()).toUpperCase();
		if((!cmd.equalsIgnoreCase("ANNOUNCEMSG"))
		&&(!cmd.equalsIgnoreCase("ANNOUNCETO"))
		&&(!cmd.equalsIgnoreCase("ANNOUNCE")))
		{
			final boolean cmdm="ANNOUNCEMSG".toUpperCase().startsWith(cmd);
			final boolean cmdt="ANNOUNCETO".toUpperCase().startsWith(cmd);
			final boolean cmd1="ANNOUNCE".toUpperCase().startsWith(cmd);
			if(cmdm&&(!cmdt)&&(!cmd1))
				cmd="ANNOUNCEMSG";
			else
			if(cmdt&&(!cmdm)&&(!cmd1))
				cmd="ANNOUNCETO";
			else
			if(cmd1&&(!cmdm)&&(!cmdt))
				cmd="ANNOUNCE";
		}
		if(cmd.equalsIgnoreCase("ANNOUNCEMSG"))
		{
			final String s=CMParms.combine(commands,1);
			if(s.length()==0)
				mob.tell(L("Your announce message is currently: @x1",mob.playerStats().getAnnounceMessage()));
			else
			{
				if(mob.playerStats()!=null)
					mob.playerStats().setAnnounceMessage(s);
				mob.tell(L("Your announce message has been changed."));
			}
		}
		else
		if(commands.size()>1)
		{
			if((!cmd.equalsIgnoreCase("ANNOUNCETO"))
			||(((String)commands.elementAt(1)).toUpperCase().equals("ALL")))
			{
				String text=null;
				if(cmd.equalsIgnoreCase("ANNOUNCETO"))
					text=CMParms.combine(commands,2);
				else
					text=CMParms.combine(commands,1);

				for(final Session S : CMLib.sessions().localOnlineIterable())
				{
					if((S.mob()!=null)
					&&(S.mob().location()!=null)
					&&(CMSecurity.isAllowed(mob,S.mob().location(),CMSecurity.SecFlag.ANNOUNCE)))
						sendAnnounce(mob,text,S);
				}
			}
			else
			{
				boolean found=false;
				final String name=(String)commands.elementAt(1);
				for(final Session S : CMLib.sessions().localOnlineIterable())
				{
					if((S.mob()!=null)
					&&(S.mob().location()!=null)
					&&(CMSecurity.isAllowed(mob,S.mob().location(),CMSecurity.SecFlag.ANNOUNCE))
					&&(((name.equalsIgnoreCase("here"))&&(S.mob().location()==mob.location()))
						||(CMLib.english().containsString(S.mob().name(),name))))
					{
						sendAnnounce(mob,CMParms.combine(commands,2),S);
						found=true;
						break;
					}
				}
				if(!found)
					mob.tell(L("You can't find anyone by that name."));
			}
		}
		else
			mob.tell(L("Usage ANNOUNCETO [ALL|HERE|(USER NAME)] (MESSAGE)\n\rANNOUNCE (MESSAGE)\n\rANNOUNCEMSG (NEW ANNOUNCE PREFIX)\n\r"));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.ANNOUNCE);}


}
