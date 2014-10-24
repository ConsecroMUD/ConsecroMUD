package com.suscipio_solutions.consecro_mud.WebMacros;
import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerAccount;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class AccountNext extends StdWebMacro
{
	@Override public String name() { return "AccountNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		if(!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
			return CMProps.getVar(CMProps.Str.MUDSTATUS);

		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("ACCOUNT");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("ACCOUNT");
			return "";
		}
		String lastID="";
		String sort=httpReq.getUrlParameter("SORTBY");
		if(sort==null) sort="";
		final Enumeration<PlayerAccount> pe=CMLib.players().accounts(sort,httpReq.getRequestObjects());
		for(;pe.hasMoreElements();)
		{
			final PlayerAccount account=pe.nextElement();
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!account.getAccountName().equals(lastID))))
			{
				httpReq.addFakeUrlParameter("ACCOUNT",account.getAccountName());
				return "";
			}
			lastID=account.getAccountName();
		}
		httpReq.addFakeUrlParameter("ACCOUNT","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
