package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerAccount;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.PlayerLibrary.ThinPlayer;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class NumPlayers extends StdWebMacro
{
	@Override public String name()	{return "NumPlayers";}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		if(parms.containsKey("ALL"))
			return ""+CMLib.sessions().getCountLocalOnline();
		if(parms.containsKey("TOTALCACHED"))
			return ""+CMLib.players().numPlayers();
		if(parms.containsKey("TOTAL"))
		{
			final Enumeration<ThinPlayer> pe=CMLib.players().thinPlayers("",httpReq.getRequestObjects());
			int x=0;
			for(;pe.hasMoreElements();pe.nextElement()) x++;
			return ""+x;
		}
		if(parms.containsKey("ACCOUNTS"))
		{
			final Enumeration<PlayerAccount> pe=CMLib.players().accounts("",httpReq.getRequestObjects());
			int x=0;
			for(;pe.hasMoreElements();pe.nextElement()) x++;
			return ""+x;
		}

		int numPlayers=0;
		for(final Session S : CMLib.sessions().localOnlineIterable())
			if((S.mob()!=null)&&(!CMLib.flags().isCloaked(S.mob())))
			   numPlayers++;
		return Integer.toString(numPlayers);
	}

}
