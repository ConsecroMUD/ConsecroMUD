package com.suscipio_solutions.consecro_mud.Commands;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;


@SuppressWarnings("rawtypes")
public class ClanWho extends Who
{
	public ClanWho(){}

	private final String[] access=I(new String[]{"CLANWHO","CLWH"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final String clanName=CMParms.combine(commands,1).toUpperCase();
		final StringBuffer msg=new StringBuffer("");
		final List<String> clanList=new XVector<String>();
		if(clanName.trim().length()>0)
		{
			final Clan C=CMLib.clans().findClan(clanName);
			if(C==null)
				mob.tell(L("There's no such clan as '@x1'.",clanName));
			else
				clanList.add(C.clanID());
		}
		else
		{
			for(final Pair<Clan,Integer> c : mob.clans())
				clanList.add(c.first.clanID());
			if(clanList.size()==0)
			{
				mob.tell(L("You need to specify a clan."));
				return false;
			}
		}
		final Set<MOB> alreadyDone=new HashSet<MOB>();
		final int[] colWidths=getShortColWidths(mob);
		for(final String clanID : clanList)
		{
			final Clan C=CMLib.clans().getClan(clanID);
			if(C!=null)
			{
				msg.append("\n\r^x").append(C.getGovernmentName()).append(" ").append(C.getName()).append("\n\r");
				msg.append(getHead(colWidths));
				for(final Session S : CMLib.sessions().localOnlineIterable())
				{
					MOB mob2=S.mob();
					if((mob2!=null)&&(mob2.soulMate()!=null))
						mob2=mob2.soulMate();

					if((mob2!=null)
					&&(!S.isStopped())
					&&((((mob2.phyStats().disposition()&PhyStats.IS_CLOAKED)==0)
						||((CMSecurity.isAllowedAnywhere(mob,CMSecurity.SecFlag.CLOAK)||CMSecurity.isAllowedAnywhere(mob,CMSecurity.SecFlag.WIZINV))&&(mob.phyStats().level()>=mob2.phyStats().level()))))
					&&(mob2.getClanRole(C.clanID())!=null)
					&&(!alreadyDone.contains(mob2))
					&&(CMLib.flags().isInTheGame(mob2,true))
					&&(mob2.phyStats().level()>0))
					{
						msg.append(showWhoShort(mob2,colWidths));
						alreadyDone.add(mob2);
					}
				}
			}
		}
		mob.tell(msg.toString());
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
