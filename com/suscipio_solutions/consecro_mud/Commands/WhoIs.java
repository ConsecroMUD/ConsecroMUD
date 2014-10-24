package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.intermud.i3.packets.Intermud;


@SuppressWarnings("rawtypes")
public class WhoIs extends Who
{
	public WhoIs(){}

	private final String[] access=I(new String[]{"WHOIS"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		String mobName=CMParms.combine(commands,1);
		if((mobName==null)||(mobName.length()==0))
		{
			mob.tell(L("whois whom?"));
			return false;
		}

		final int x=mobName.indexOf("@");
		if(x>=0)
		{
			if((!(CMLib.intermud().i3online()))
			&&(!CMLib.intermud().imc2online()))
				mob.tell(L("Intermud is unavailable."));
			else
			if(x==0)
				CMLib.intermud().i3who(mob,mobName.substring(1));
			else
			{
				String mudName=mobName.substring(x+1);
				mobName=mobName.substring(0,x);
				if(Intermud.isAPossibleMUDName(mudName))
				{
					mudName=Intermud.translateName(mudName);
					if(!Intermud.isUp(mudName))
					{
						mob.tell(L("@x1 is not available.",mudName));
						return false;
					}
				}
				CMLib.intermud().i3finger(mob,mobName,mudName);
			}
			return false;
		}

		final int[] colWidths=getShortColWidths(mob);
		final StringBuffer msg=new StringBuffer("");
		for(final Session S : CMLib.sessions().localOnlineIterable())
		{
			final MOB mob2=S.mob();
			if((mob2!=null)
			&&(((mob2.phyStats().disposition()&PhyStats.IS_CLOAKED)==0)
				||((CMSecurity.isAllowedAnywhere(mob,CMSecurity.SecFlag.CLOAK)||CMSecurity.isAllowedAnywhere(mob,CMSecurity.SecFlag.WIZINV))
					&&(mob.phyStats().level()>=mob2.phyStats().level())))
			&&(mob2.phyStats().level()>0)
			&&(mob2.name().toUpperCase().startsWith(mobName.toUpperCase())))
				msg.append(showWhoShort(mob2,colWidths));
		}
		if(msg.length()==0)
			mob.tell(L("That person doesn't appear to be online.\n\r"));
		else
		{
			mob.tell(getHead(colWidths)+msg.toString());
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
