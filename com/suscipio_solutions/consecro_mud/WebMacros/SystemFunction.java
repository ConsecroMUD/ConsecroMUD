package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class SystemFunction extends StdWebMacro
{
	@Override public String name() { return "SystemFunction"; }
	@Override public boolean isAdminMacro()	{return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		if(parms.get("ANNOUNCE")!=null)
		{
			final String s=httpReq.getUrlParameter("TEXT");
			if((s!=null)&&(s.length()>0))
			{
				final MOB M=((MOB)CMClass.sampleMOB().copyOf());
				final Command C=CMClass.getCommand("Announce");
				try
				{
					C.execute(M,CMParms.parse("all "+s.trim()),0);
				}catch(final Exception e){}
			}
		}
		if(parms.get("SHUTDOWN")!=null)
		{
com.suscipio_solutions.consecro_mud.application.MUD.globalShutdown(null,(parms.get("RESTART")==null),null);
			return "";
		}
		return "";
	}
}
