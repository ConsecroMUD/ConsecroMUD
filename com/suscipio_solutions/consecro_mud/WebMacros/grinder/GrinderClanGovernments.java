package com.suscipio_solutions.consecro_mud.WebMacros.grinder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Common.interfaces.ClanGovernment;
import com.suscipio_solutions.consecro_mud.Common.interfaces.ClanPosition;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class GrinderClanGovernments
{
	public String name() { return "GrinderClanGovernments"; }

	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("GOVERNMENT");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			ClanGovernment G=null;
			if(CMath.isInteger(last))
			{
				final int lastID=CMath.s_int(last);
				G=CMLib.clans().getStockGovernment(lastID);
			}
			else
			if((httpReq.getUrlParameter("NAME")==null)||httpReq.getUrlParameter("NAME").length()==0)
				return " @break@";
			else
			{
				final Set<Integer> usedTypeIDs=new HashSet<Integer>();
				for(final ClanGovernment G2 : CMLib.clans().getStockGovernments())
					usedTypeIDs.add(Integer.valueOf(G2.getID()));
				G=CMLib.clans().createGovernment(httpReq.getUrlParameter("NAME"));
				for(int i=0;i<CMLib.clans().getStockGovernments().length;i++)
					if(!usedTypeIDs.contains(Integer.valueOf(i)))
					{
						httpReq.addFakeUrlParameter("GOVERNMENT", Integer.toString(i));
						G.setID(i);
						break;
					}
			}

			String str=null;
			str=httpReq.getUrlParameter("NAME");
			if(str!=null) G.setName(str);
			str=httpReq.getUrlParameter("CATEGORY");
			if(str!=null) G.setCategory(str);
			str=httpReq.getUrlParameter("ACCEPTPOS");
			if(str!=null) G.setAcceptPos(CMath.s_int(str));
			str=httpReq.getUrlParameter("AUTOROLE");
			if(str!=null) G.setAutoRole(CMath.s_int(str));
			str=httpReq.getUrlParameter("SHORTDESC");
			if(str!=null) G.setShortDesc(str);
			str=httpReq.getUrlParameter("REQUIREDMASK");
			if(str!=null){ G.setRequiredMaskStr(str);}
			str=httpReq.getUrlParameter("ENTRYSCRIPT");
			if(str!=null){ G.setEntryScript(str);}
			str=httpReq.getUrlParameter("EXITSCRIPT");
			if(str!=null){ G.setExitScript(str);}
			str=httpReq.getUrlParameter("ISPUBLIC");
			G.setPublic((str==null)?false:str.equalsIgnoreCase("on"));
			str=httpReq.getUrlParameter("ISFAMILYONLY");
			G.setFamilyOnly((str==null)?false:str.equalsIgnoreCase("on"));
			str=httpReq.getUrlParameter("XPLEVELFORMULA");
			if(str!=null) G.setXpCalculationFormulaStr(str);
			str=httpReq.getUrlParameter("OVERRIDEMINMEMBERS");
			if(str!=null) G.setOverrideMinMembers((str.trim().length()==0)?null:Integer.valueOf(CMath.s_int(str)));
			str=httpReq.getUrlParameter("CONQUESTENABLED");
			G.setConquestEnabled((str==null)?false:str.equalsIgnoreCase("on"));
			str=httpReq.getUrlParameter("CONQUESTITEMLOYALTY");
			G.setConquestItemLoyalty((str==null)?false:str.equalsIgnoreCase("on"));
			str=httpReq.getUrlParameter("CONQUESTDEITYBASIS");
			G.setConquestByWorship((str==null)?false:str.equalsIgnoreCase("on"));
			str=httpReq.getUrlParameter("ISRIVALROUS");
			G.setRivalrous((str==null)?false:str.equalsIgnoreCase("on"));
			str=httpReq.getUrlParameter("MAXVOTEDAYS");
			if(str!=null) G.setMaxVoteDays(CMath.s_int(str));
			str=httpReq.getUrlParameter("VOTEQUORUMPCT");
			if(str!=null) G.setVoteQuorumPct(CMath.s_int(str));
			str=httpReq.getUrlParameter("AUTOPROMOTEBY");
			if(str!=null) G.setAutoPromoteBy((Clan.AutoPromoteFlag)CMath.s_valueOf(Clan.AutoPromoteFlag.values(), str));
			str=httpReq.getUrlParameter("LONGDESC");
			if(str!=null) G.setLongDesc(str);
			final String old=httpReq.getUrlParameter("VOTEFUNCS");
			final Set<String> voteFuncs=new HashSet<String>();
			if((old!=null)&&(old.length()>0))
			{
				voteFuncs.add(old);
				int x=1;
				while(httpReq.getUrlParameter("VOTEFUNCS"+x)!=null)
				{
					voteFuncs.add(httpReq.getUrlParameter("VOTEFUNCS"+x));
					x++;
				}
			}

			final List<ClanPosition> posList=new Vector<ClanPosition>();
			String posDexStr="0";
			int posDex=0;
			while(httpReq.isUrlParameter("GPOSID_"+posDexStr) && httpReq.getUrlParameter("GPOSID_"+posDexStr).trim().length()>0)
			{
				final String oldID=httpReq.getUrlParameter("GPOSID_"+posDexStr);
				final String oldName=httpReq.getUrlParameter("GPOSNAME_"+posDexStr);
				final String oldPluralName=httpReq.getUrlParameter("GPOSPLURALNAME_"+posDexStr);
				final int oldRoleID=CMath.s_int(httpReq.getUrlParameter("GPOSROLEID_"+posDexStr));
				final int oldRank=CMath.s_int(httpReq.getUrlParameter("GPOSRANK_"+posDexStr));
				final int oldMax=CMath.s_int(httpReq.getUrlParameter("GPOSMAX_"+posDexStr));
				final String oldMask=httpReq.getUrlParameter("GPOSINNERMASK_"+posDexStr);
				final String oldIsPublicStr=httpReq.getUrlParameter("GPOSISPUBLIC_"+posDexStr);
				final boolean oldIsPublic=oldIsPublicStr==null?false:oldIsPublicStr.equalsIgnoreCase("on");
				final Clan.Authority powerFuncs[]=new Clan.Authority[Clan.Function.values().length];
				for(int f=0;f<Clan.Function.values().length;f++)
					powerFuncs[f]=Clan.Authority.CAN_NOT_DO;
				String authDexStr="";
				int authDex=0;
				while(httpReq.getUrlParameter("GPOSPOWER_"+posDexStr+"_"+authDexStr)!=null)
				{
					final Clan.Function auth = (Clan.Function)CMath.s_valueOf(Clan.Function.values(),httpReq.getUrlParameter("GPOSPOWER_"+posDexStr+"_"+authDexStr));
					powerFuncs[auth.ordinal()]=Clan.Authority.CAN_DO;
					authDex++;
					authDexStr=Integer.toString(authDex);
				}
				for(final String s : voteFuncs)
				{
					final Clan.Function auth = (Clan.Function)CMath.s_valueOf(Clan.Function.values(),s);
					powerFuncs[auth.ordinal()]=Clan.Authority.MUST_VOTE_ON;
				}
				final ClanPosition P=(ClanPosition)CMClass.getCommon("DefaultClanPosition");
				P.setID(oldID);
				P.setRoleID(oldRoleID);
				P.setRank(oldRank);
				P.setName(oldName);
				P.setPluralName(oldPluralName);
				P.setMax(oldMax);
				P.setInnerMaskStr(oldMask);
				P.setFunctionChart(powerFuncs);
				P.setPublic(oldIsPublic);
				posList.add(P);
				posDex++;
				posDexStr=Integer.toString(posDex);
			}
			G.setPositions(posList.toArray(new ClanPosition[0]));
			GrinderRaces.setDynAbilities(G,httpReq);
			GrinderRaces.setDynEffects(G,httpReq);
		}

		return "";
	}
}
