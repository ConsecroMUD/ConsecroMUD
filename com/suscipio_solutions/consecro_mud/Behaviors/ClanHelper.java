package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.collections.Triad;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;


public class ClanHelper extends StdBehavior
{
	@Override public String ID(){return "ClanHelper";}

	@Override
	public String accountForYourself()
	{
		if(parms.length()>0)
			return "fellow '"+parms+"' protecting";
		else
			return "fellow clan members protecting";
	}

	protected boolean mobKiller=false;
	@Override
	public void startBehavior(PhysicalAgent forMe)
	{
		super.startBehavior(forMe);
		if(forMe instanceof MOB)
		{
			if(parms.length()>0)
			{
				Clan C=CMLib.clans().getClan(parms.trim());
				if(C==null)
					C=CMLib.clans().findClan(parms.trim());
				if(C!=null)
					((MOB)forMe).setClan(C.clanID(),C.getGovernment().getAcceptPos());
				else
					Log.errOut("ClanHelper","Unknown clan "+parms+" for "+forMe.Name()+" in "+CMLib.map().getExtendedRoomID(CMLib.map().roomLocation(forMe)));
			}
		}
	}

	@Override
	public void executeMsg(Environmental affecting, CMMsg msg)
	{
		super.executeMsg(affecting,msg);
		if((msg.target()==null)||(!(msg.target() instanceof MOB))) return;
		final MOB source=msg.source();
		final MOB observer=(MOB)affecting;
		final MOB target=(MOB)msg.target();

		if((target==null)||(observer==null)) return;
		if((source!=observer)
		&&(CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
		&&(target!=observer)
		&&(source!=target)
		&&(!observer.isInCombat())
		&&(CMLib.flags().canBeSeenBy(source,observer))
		&&(CMLib.flags().canBeSeenBy(target,observer))
		&&(!BrotherHelper.isBrother(source,observer,false)))
		{
			final List<Triad<Clan,Integer,Integer>> list=CMLib.clans().findCommonRivalrousClans(observer, target);
			if(list.size()>0)
			{
				Clan C=null;
				for(final Triad<Clan,Integer,Integer> t : list)
					if(source.getClanRole(t.first.clanID())==null)
					{
						C=t.first;
						break;
					}
				String reason="WE ARE UNDER ATTACK!! CHARGE!!";
				if(C!=null)
					reason=C.getName().toUpperCase()+"S UNITE! CHARGE!";
				Aggressive.startFight(observer,source,true,false,reason);
			}
		}
	}
}
