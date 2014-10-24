package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.AccountStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.TimeClock;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class PrideStat extends StdWebMacro
{
	@Override public String name() { return "PrideStat"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		TimeClock.TimePeriod period=null;
		AccountStats.PrideStat stat=null;
		int which=-1;
		String val=null;
		boolean player = true;
		for(final String s : parms.keySet())
		{
			if(CMath.isInteger(s))
				which=CMath.s_int(s.trim());
			else
			if(s.equalsIgnoreCase("account"))
				player=false;
			else
			if(s.equalsIgnoreCase("player"))
				player=true;
			else
			if(s.equalsIgnoreCase("name"))
				val="NAME";
			else
			if(s.equalsIgnoreCase("value"))
				val="VALUE";
			else
			{
				try
				{
					period=TimeClock.TimePeriod.valueOf(s.toUpperCase().trim());
				}
				catch(final Exception e)
				{
					try
					{
						stat=AccountStats.PrideStat.valueOf(s.toUpperCase().trim());
					}
					catch(final Exception e2)
					{
						return " [error unknown parameter: "+s+"]";
					}
				}
			}
		}
		if(period==null)
			return " [error missing valid period, try "+CMParms.toStringList(TimeClock.TimePeriod.values())+"]";
		if(stat==null)
			return " [error missing valid stat, try "+CMParms.toStringList(AccountStats.PrideStat.values())+"]";
		if(val==null)
			return " [error missing value type, try name or value]";
		if(which<1)
			return " [error missing number, try 1-10]";

		final List<Pair<String,Integer>> list=player?CMLib.players().getTopPridePlayers(period, stat):CMLib.players().getTopPrideAccounts(period, stat);
		if(which>list.size())
			return "";
		final Pair<String,Integer> p=list.get(which-1);
		if(val.equals("NAME"))
			return p.first;
		else
			return p.second.toString();
	}
}
