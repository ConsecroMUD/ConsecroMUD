package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Iterator;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan.Function;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Banker;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.interfaces.ShopKeeper;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings("rawtypes")
public class BankChainNext extends StdWebMacro
{
	@Override public String name() { return "BankChainNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		MOB playerM=null;
		boolean destroyPlayer=false;
		try
		{
		final java.util.Map<String,String> parms=parseParms(parm);
		String last=httpReq.getUrlParameter("BANKCHAIN");
		String player=httpReq.getUrlParameter("PLAYER");
		if((player==null)||(player.length()==0)) player=httpReq.getUrlParameter("CLAN");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("BANKCHAIN");
			return "";
		}
		String lastID="";
		final MOB M = Authenticate.getAuthenticatedMob(httpReq);
		if(M==null) return " @break@";
		if((player!=null)&&(player.length()>0))
		{
			if(((!M.Name().equalsIgnoreCase(player)))
			&&(!CMSecurity.isAllowedEverywhere(M,CMSecurity.SecFlag.CMDPLAYERS)))
				return "";
			final Clan C=CMLib.clans().getClan(player);
			if(C!=null)
			{
				playerM=CMClass.getFactoryMOB();
				playerM.setName(C.clanID());
				playerM.setLocation(M.location());
				playerM.setStartRoom(M.getStartRoom());
				playerM.setClan(C.clanID(),C.getTopRankedRoles(Function.DEPOSIT_LIST).get(0).intValue());
				destroyPlayer=true;
			}
			else
			{
				playerM=CMLib.players().getPlayer(player);
				if(playerM==null)
				{
					playerM=CMClass.getFactoryMOB();
					playerM.setName(CMStrings.capitalizeAndLower(player));
					playerM.setLocation(M.location());
					playerM.setStartRoom(M.getStartRoom());
					destroyPlayer=true;
				}
			}
		}
		else
		if(!CMSecurity.isAllowedEverywhere(M,CMSecurity.SecFlag.CMDPLAYERS))
			return "";

		for(final Iterator j=CMLib.map().bankChains(null);j.hasNext();)
		{
			final String bankChain=(String)j.next();
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!bankChain.equals(lastID))))
			{
				httpReq.addFakeUrlParameter("BANKCHAIN",bankChain);
				last=bankChain;
				if(playerM!=null)
				{
					final Banker bankerM=CMLib.map().getBank(bankChain,bankChain);
					if((bankerM==null)
					||((bankerM.isSold(ShopKeeper.DEAL_CLANBANKER))&&(playerM.getClanRole(playerM.Name())==null))
					||(BankAccountInfo.getMakeAccountInfo(httpReq,bankerM,playerM).balance<=0.0))
					{
						lastID=bankChain;
						continue;
					}
				}
				return "";
			}
			lastID=bankChain;
		}
		httpReq.addFakeUrlParameter("BANKCHAIN","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
		}
		finally
		{
			if((destroyPlayer)&&(playerM!=null))
			{
				playerM.setLocation(null);
				playerM.destroy();
			}
		}
	}

}
