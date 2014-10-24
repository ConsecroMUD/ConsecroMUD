package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class BankChainName extends StdWebMacro
{
	@Override public String name() { return "BankChainName"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("BANKCHAIN");
		if(last==null) return " @break@";
		return clearWebMacros(last);
	}
}
