package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan.Authority;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;


@SuppressWarnings({"unchecked","rawtypes"})
public class ClanDonateSet extends StdCommand
{
	public ClanDonateSet(){}

	private final String[] access=I(new String[]{"CLANDONATESET"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final String clanName=(commands.size()>1)?CMParms.combine(commands,1,commands.size()):"";

		Clan C=null;
		final boolean skipChecks=mob.getClanRole(mob.Name())!=null;
		if(skipChecks) C=mob.getClanRole(mob.Name()).first;

		if(C==null)
		for(final Pair<Clan,Integer> c : mob.clans())
			if((clanName.length()==0)||(CMLib.english().containsString(c.first.getName(), clanName))
			&&(c.first.getAuthority(c.second.intValue(), Clan.Function.SET_DONATE)!=Authority.CAN_NOT_DO))
			{	C=c.first; break; }

		commands.setElementAt(getAccessWords()[0],0);

		Room R=mob.location();
		if(skipChecks)
			R=CMLib.map().getRoom(CMParms.combine(commands,1));
		else
		{
			commands.clear();
			commands.addElement(getAccessWords()[0]);
			commands.addElement(CMLib.map().getExtendedRoomID(R));
		}

		if(C==null)
		{
			mob.tell(L("You aren't allowed to set a donation room for @x1.",((clanName.length()==0)?"anything":clanName)));
			return false;
		}

		if(C.getStatus()>Clan.CLANSTATUS_ACTIVE)
		{
			mob.tell(L("You cannot set a donation room.  Your @x1 does not have enough members to be considered active.",C.getGovernmentName()));
			return false;
		}
		if(skipChecks||CMLib.clans().goForward(mob,C,commands,Clan.Function.SET_DONATE,false))
		{
			if(!CMLib.law().doesOwnThisProperty(C.clanID(),R))
			{
				mob.tell(L("Your @x1 does not own this room.",C.getGovernmentName()));
				return false;
			}
			if(skipChecks||CMLib.clans().goForward(mob,C,commands,Clan.Function.SET_DONATE,true))
			{
				C.setDonation(CMLib.map().getExtendedRoomID(R));
				C.update();
				mob.tell(L("The donation room for @x1 @x2 is now set to @x3.",C.getGovernmentName(),C.clanID(),R.displayText(mob)));
				CMLib.clans().clanAnnounce(mob,L("The donation room for @x1 @x2 is now set to @x3.",C.getGovernmentName(),C.clanID(),R.displayText(mob)));
				return true;
			}
		}
		else
		{
			mob.tell(L("You aren't in the right position to set your @x1's donation room.",C.getGovernmentName()));
			return false;
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return false;}


}
