package com.suscipio_solutions.consecro_mud.WebMacros.grinder;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan.MemberRecord;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings({"unchecked","rawtypes"})
public class GrinderClans
{
	public String name() { return "GrinderClans"; }

	public static String membersList(Clan C, HTTPRequest httpReq)
	{
		final Vector newMembersNames=new Vector();
		final List<MemberRecord> DV=C.getMemberList();
		if(httpReq.isUrlParameter("MEMB1"))
		{
			int num=1;
			String aff=httpReq.getUrlParameter("MEMB"+num);
			while(aff!=null)
			{
				if(aff.length()>0)
				{
					final MOB M=CMLib.players().getLoadPlayer(aff);
					if(M==null) return "Unknown player '"+aff+"'.";
					newMembersNames.addElement(M.Name());
					final int newRole=CMath.s_int(httpReq.getUrlParameter("ROLE"+num));
					C.addMember(M,newRole);
				}
				num++;
				aff=httpReq.getUrlParameter("MEMB"+num);
			}
			for(final MemberRecord member : DV)
			{
				if(!newMembersNames.contains(member.name))
				{
					final MOB M=CMLib.players().getLoadPlayer(member.name);
					if(M!=null) C.delMember(M);
				}
			}
		}
		return "";
	}

	public static String relationsList(Clan C, HTTPRequest httpReq)
	{
		if(httpReq.isUrlParameter("RELATION1"))
		{
			int relat=0;
			Clan CC=null;
			for(final Enumeration e=CMLib.clans().clans();e.hasMoreElements();)
			{
				CC=(Clan)e.nextElement();
				if(CC==C) continue;
				relat++;
				final String aff=httpReq.getUrlParameter("RELATION"+relat);
				if((aff!=null)&&(aff.length()>0))
				{
					if(C.getClanRelations(CC.clanID())!=CMath.s_int(aff))
						C.setClanRelations(CC.clanID(),CMath.s_int(aff),System.currentTimeMillis());
				}
				else
					return "No relation for clan "+CC.clanID();
			}
		}
		return "";
	}

	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("CLAN");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			final Clan C=CMLib.clans().getClan(last);
			if(C!=null)
			{
				String str=null;
				str=httpReq.getUrlParameter("PREMISE");
				if(str!=null) C.setPremise(str);
				str=httpReq.getUrlParameter("RECALLID");
				if(str!=null)
				{
					final Room R=CMLib.map().getRoom(str);
					if(R!=null) C.setRecall(CMLib.map().getExtendedRoomID(R));
				}
				str=httpReq.getUrlParameter("MORGUEID");
				if(str!=null)
				{
					final Room R=CMLib.map().getRoom(str);
					if(R!=null) C.setMorgue(CMLib.map().getExtendedRoomID(R));
				}
				str=httpReq.getUrlParameter("AUTOPOSITIONID");
				if(str!=null) C.setAutoPosition(CMath.s_int(str));
				str=httpReq.getUrlParameter("DONATIONID");
				if(str!=null)
				{
					final Room R=CMLib.map().getRoom(str);
					if(R!=null) C.setDonation(CMLib.map().getExtendedRoomID(R));
				}
				str=httpReq.getUrlParameter("TAX");
				if(str!=null) C.setTaxes(CMath.s_pct(str));
				str=httpReq.getUrlParameter("CCLASSID");
				if(str!=null)
				{
					CharClass CC=CMClass.getCharClass(str);
					if(CC==null)CC=CMClass.findCharClass(str);
					if(CC!=null) C.setClanClass(CC.ID());
				}
				str=httpReq.getUrlParameter("EXP");
				if(str!=null) C.setExp(CMath.s_int(str));
				str=httpReq.getUrlParameter("CATEGORY");
				if(str!=null) C.setCategory(str);
				str=httpReq.getUrlParameter("MINMEMBERS");
				if(str!=null) C.setMinClanMembers(CMath.s_int(str));
				str=httpReq.getUrlParameter("ISRIVALROUS");
				if(str!=null) C.setRivalrous(str.equalsIgnoreCase("on"));
				str=httpReq.getUrlParameter("STATUSID");
				if(str!=null) C.setStatus(CMath.s_int(str));
				str=httpReq.getUrlParameter("ACCEPTANCEID");
				if(str!=null) C.setAcceptanceSettings(str);
				str=httpReq.getUrlParameter("TYPEID");
				if(str!=null) C.setGovernmentID(CMath.s_int(str));
				String err=GrinderClans.membersList(C,httpReq);
				if(err.length()>0) return err;
				err=GrinderClans.relationsList(C,httpReq);
				if(err.length()>0) return err;
			}
		}
		return "";
	}
}
