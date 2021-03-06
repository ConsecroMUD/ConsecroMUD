package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan.Authority;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan.Function;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan.MemberRecord;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;


@SuppressWarnings({"unchecked","rawtypes"})
public class ClanAssign extends StdCommand
{
	public ClanAssign(){}

	private final String[] access=I(new String[]{"CLANASSIGN"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<3)
		{
			mob.tell(L("You must specify the members name, and a new role."));
			return false;
		}
		String memberStr=(commands.size()>2)?(String)commands.get(commands.size()-2):"";
		final String pos=(commands.size()>1)?(String)commands.get(commands.size()-1):"";
		final String clanName=(commands.size()>3)?CMParms.combine(commands,1,commands.size()-2):"";

		Clan C=null;
		final boolean skipChecks=mob.getClanRole(mob.Name())!=null;
		if(skipChecks) C=mob.getClanRole(mob.Name()).first;

		if(C==null)
		for(final Pair<Clan,Integer> c : mob.clans())
			if((clanName.length()==0)||(CMLib.english().containsString(c.first.getName(), clanName))
			&&(c.first.getAuthority(c.second.intValue(), Clan.Function.ASSIGN)!=Authority.CAN_NOT_DO))
			{	C=c.first; break; }

		commands.clear();
		commands.add(getAccessWords()[0]);
		commands.add(memberStr);
		commands.add(pos);
		final StringBuffer msg=new StringBuffer("");
		boolean found=false;
		if(memberStr.length()>0)
		{
			if(C==null)
			{
				mob.tell(L("You aren't allowed to assign anyone from @x1.",((clanName.length()==0)?"anything":clanName)));
				return false;
			}
			if(skipChecks||CMLib.clans().goForward(mob,C,commands,Clan.Function.ASSIGN,false))
			{
				final List<MemberRecord> members=C.getMemberList();
				if(members.size()<1)
				{
					mob.tell(L("There are no members in your @x1",C.getGovernmentName()));
					return false;
				}
				final int newPos=C.getRoleFromName(pos);
				if(newPos<0)
				{
					mob.tell(L("'@x1' is not a valid role.",pos));
					return false;
				}
				memberStr=CMStrings.capitalizeAndLower(memberStr);
				for(final MemberRecord member : members)
				{
					if(member.name.equalsIgnoreCase(memberStr))
					{
						found=true;
					}
				}
				if(found)
				{
					final MOB M=CMLib.players().getLoadPlayer(memberStr);
					final Pair<Clan,Integer> oldRole=(M!=null)?M.getClanRole(C.clanID()):null;
					if((M==null)||(oldRole==null))
					{
						mob.tell(L("@x1 was not found.  Could not change @x2 role.",memberStr,C.getGovernmentName()));
						return false;
					}
					if(!C.canBeAssigned(M, newPos))
					{
						mob.tell(L("@x1 may not be assigned to @x2.",M.name(mob),C.getRoleName(newPos,true,false)));
						return false;
					}
					if(skipChecks||CMLib.clans().goForward(mob,C,commands,Clan.Function.ASSIGN,true))
					{
						final int oldPos=oldRole.second.intValue();
						final int maxInNewPos=C.getMostInRole(newPos);
						final Vector currentMembersInNewPosV=new Vector();
						for(final MemberRecord member : members)
							if(member.role==newPos)
								currentMembersInNewPosV.addElement(member.name);
						final List<Integer> topRoleIDs=C.getTopRankedRoles(Function.ASSIGN);
						if(topRoleIDs.contains(Integer.valueOf(oldPos)))
						{ // If you WERE already the highest order.. you must be being demoted.
							// so we check to see if there will be any other high officers left
							int numMembers=0;
							for(final MemberRecord member : members)
								if(!M.Name().equalsIgnoreCase(member.name))
									if(topRoleIDs.contains(Integer.valueOf(member.role)))
										numMembers++;
							if(numMembers==0)
							{
								mob.tell(L("@x1 is the last @x2 and must be replaced before being reassigned.",M.Name(),C.getRoleName(oldPos,true,false)));
								return false;
							}
						}
						if((currentMembersInNewPosV.size()>0)&&(maxInNewPos<Integer.MAX_VALUE))
						{
							// if there are too many in the new position, demote some of them.
							while(currentMembersInNewPosV.size()>=maxInNewPos)
							{
								final String s=(String)currentMembersInNewPosV.elementAt(0);
								currentMembersInNewPosV.removeElementAt(0);
								CMLib.clans().clanAnnounce(mob,L(" @x1 of the @x2 @x3 is now a @x4.",s,C.getGovernmentName(),C.clanID(),C.getRoleName(C.getGovernment().getAcceptPos(),true,false)));
								final MOB M2=CMLib.players().getPlayer(s);
								if(M2!=null) M2.setClan(C.clanID(),C.getGovernment().getAcceptPos());
								CMLib.database().DBUpdateClanMembership(s, C.clanID(), C.getGovernment().getAcceptPos());
								C.updateClanPrivileges(M2);
							}
						}
						// finally, promote
						CMLib.clans().clanAnnounce(mob,L("@x1 of the @x2 @x3 changed from @x4 to @x5.",M.name(),C.getGovernmentName(),C.clanID(),C.getRoleName(oldRole.second.intValue(),true,false),C.getRoleName(newPos,true,false)));
						C.addMember(M,newPos);
						mob.tell(L("@x1 of the @x2 @x3 has been assigned to be @x4. ",M.Name(),C.getGovernmentName(),C.clanID(),CMLib.english().startWithAorAn(C.getRoleName(newPos,false,false))));
						if((M.session()!=null)&&(M.session().mob()==M))
							M.tell(L("You have been assigned to be @x1 of @x2 @x3.",CMLib.english().startWithAorAn(C.getRoleName(newPos,false,false)),C.getGovernmentName(),C.clanID()));
						return false;
					}
				}
				else
				{
					msg.append(L("@x1 isn't a member of your @x2.",memberStr,C.getGovernmentName()));
				}
			}
			else
			{
				msg.append(L("You aren't in the right position to assign anyone in your @x1.",C.getGovernmentName()));
			}
		}
		else
		{
			msg.append(L("You haven't specified which member you are assigning a new role to."));
		}
		mob.tell(msg.toString());
		return false;
	}

	@Override public boolean canBeOrdered(){return false;}


}
