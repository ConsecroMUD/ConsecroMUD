package com.suscipio_solutions.consecro_mud.WebMacros;
import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerAccount;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.PlayerLibrary;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings("rawtypes")
public class AccountPlayerNext extends StdWebMacro
{
	@Override public String name() { return "AccountPlayerNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		if(!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
			return CMProps.getVar(CMProps.Str.MUDSTATUS);

		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("PLAYER");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("PLAYER");
			return "";
		}
		final String accountName=httpReq.getUrlParameter("ACCOUNT");
		if(accountName==null) return " @break@";
		final PlayerAccount account=CMLib.players().getLoadAccount(accountName);
		if(account==null) return "";

		String lastID="";
		String sort=httpReq.getUrlParameter("SORTBY");
		if(sort==null) sort="";
		final Enumeration pe=account.getThinPlayers();
		for(;pe.hasMoreElements();)
		{
			final PlayerLibrary.ThinPlayer user=(PlayerLibrary.ThinPlayer)pe.nextElement();
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!user.name.equals(lastID))))
			{
				httpReq.addFakeUrlParameter("PLAYER",user.name);
				return "";
			}
			lastID=user.name;
		}
		httpReq.addFakeUrlParameter("PLAYER","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
