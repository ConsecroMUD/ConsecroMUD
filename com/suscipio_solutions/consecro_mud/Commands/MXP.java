package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Resources;


@SuppressWarnings("rawtypes")
public class MXP extends StdCommand
{
	public MXP(){}

	private final String[] access=I(new String[]{"MXP"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(!mob.isMonster())
		{
			if((!mob.isAttribute(MOB.Attrib.MXP))
			||(!mob.session().getClientTelnetMode(Session.TELNET_MXP)))
			{
				mob.session().changeTelnetMode(Session.TELNET_MXP,true);
				if(mob.session().getTerminalType().toLowerCase().startsWith("mushclient"))
					mob.session().negotiateTelnetMode(Session.TELNET_MXP);
				for(int i=0;((i<5)&&(!mob.session().getClientTelnetMode(Session.TELNET_MXP)));i++)
				{
					try{mob.session().prompt("",250);}catch(final Exception e){}
				}
				if(mob.session().getClientTelnetMode(Session.TELNET_MXP))
				{
					mob.setAttribute(MOB.Attrib.MXP,true);
					final StringBuffer mxpText=Resources.getFileResource("text/mxp.txt",true);
					if(mxpText!=null)
						mob.session().rawOut("\033[6z\n\r"+mxpText.toString()+"\n\r");
					mob.tell(L("MXP codes enabled.\n\r"));
				}
				else
					mob.tell(L("Your client does not appear to support MXP."));
			}
			else
				mob.tell(L("MXP codes are already enabled.\n\r"));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return super.securityCheck(mob)&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.MXP));}
}

