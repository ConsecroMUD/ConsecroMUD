package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan.Function;
import com.suscipio_solutions.consecro_mud.Common.interfaces.TimeClock;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.MoneyLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Banker;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ShopKeeper;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class BankAccountInfo extends StdWebMacro
{
	@Override public String name() { return "BankAccountInfo"; }

	public static class BankAccountStuff
	{
		double balance=0.0;
		MoneyLibrary.DebtItem debt=null;
		List<Item> items=new Vector<Item>(1);
	}

	public static synchronized BankAccountStuff getMakeAccountInfo(HTTPRequest httpReq, Banker B, MOB playerM)
	{
		BankAccountStuff info=(BankAccountStuff)httpReq.getRequestObjects().get("BANKINFO: "+B.bankChain()+": "+playerM.Name());
		if(info!=null) return info;
		info=new BankAccountStuff();
		if((B.isSold(ShopKeeper.DEAL_CLANBANKER))&&(playerM.getClanRole(playerM.Name())==null))
		{
		}
		else
		{
			final Double bal=Double.valueOf(B.getBalance(playerM.Name())); // this works for clans because name==clan name
			info.balance=bal.doubleValue();
			info.debt=B.getDebtInfo(playerM.Name());
			info.items=B.getDepositedItems(playerM.Name());
		}
		httpReq.getRequestObjects().put("BANKINFO: "+B.bankChain()+": "+playerM.Name(),info);
		return info;
	}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		MOB playerM=null;
		Area playerA=null;
		boolean destroyPlayer=false;
		try
		{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("BANKCHAIN");
		if(last==null) return " @break@";
		final MOB M = Authenticate.getAuthenticatedMob(httpReq);
		if(M==null) return " @break@";
		String player=httpReq.getUrlParameter("PLAYER");
		if((player==null)||(player.length()==0))
			player=httpReq.getUrlParameter("CLAN");
		final Banker B=CMLib.map().getBank(last,last);
		if(B==null) return "BANKER not found?!";
		if((player!=null)&&(player.length()>0))
		{
			if((!M.Name().equalsIgnoreCase(player))
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
					playerM.setClan("", Integer.MIN_VALUE); // delete all sequence
					destroyPlayer=true;
				}
			}
			playerA=CMLib.map().getStartArea(playerM);
			if(playerA==null)
				return "PLAYER not found!";
		}
		else
			return "PLAYER not set!";
		final BankAccountStuff acct=BankAccountInfo.getMakeAccountInfo(httpReq,B,playerM);
		final double balance=acct.balance;
		if(parms.containsKey("HASACCT"))
			return (balance>0.0)?"true":"false";
		if(balance<=0.0) return "";
		if(parms.containsKey("BALANCE"))
			return CMLib.beanCounter().nameCurrencyLong(playerM,balance);
		if((parms.containsKey("DEBTAMT"))
		||(parms.containsKey("DEBTRSN"))
		||(parms.containsKey("DEBTDUE"))
		||(parms.containsKey("DEBTINT")))
		{
			final MoneyLibrary.DebtItem debt=acct.debt;
			if((debt==null)||(debt.amt==0.0)) return "N/A";
			final double amt=debt.amt;
			final String reason=debt.reason;
			final String intRate=CMath.div((int)Math.round(debt.interest*10000.0),100.0)+"%";
			final long dueLong=debt.due;
			final long timeRemaining=System.currentTimeMillis()-dueLong;
			String dueDate="";
			if(timeRemaining<0)
				dueDate="Past due.";
			else
			{
				final int mudHoursToGo=(int)(timeRemaining/CMProps.getMillisPerMudHour());
				if(playerA.getTimeObj()==null)
					dueDate="Not available";
				else
				{
					final TimeClock T=(TimeClock)playerA.getTimeObj().copyOf();
					T.tickTock(mudHoursToGo);
					dueDate=T.getShortTimeDescription();
				}
			}
			if(parms.containsKey("DEBTAMT")) return CMLib.beanCounter().nameCurrencyLong(playerM,amt);
			if(parms.containsKey("DEBTRSN")) return reason;
			if(parms.containsKey("DEBTDUE")) return dueDate;
			if(parms.containsKey("DEBTINT")) return intRate;
		}
		if(parms.containsKey("NUMITEMS")) return ""+(B.getDepositedItems(playerM.Name()).size()-1);
		if(parms.containsKey("ITEMSWORTH")) return CMLib.beanCounter().nameCurrencyLong(playerM,B.totalItemsWorth(playerM.Name()));
		if(parms.containsKey("ITEMSLIST"))
		{
			final List<Item> items=acct.items;
			if(items != null)
			{
				final StringBuffer list=new StringBuffer("");
				for(int v=0;v<items.size();v++)
					if(!(items.get(v) instanceof Coins))
					{
						list.append(((Environmental)items.get(v)).name());
						if(v<(items.size()-1)) list.append(", ");
					}
				return list.toString();
			}
		}
		return "";
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
