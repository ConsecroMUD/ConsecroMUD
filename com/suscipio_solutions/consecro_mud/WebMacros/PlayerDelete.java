package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class PlayerDelete extends StdWebMacro
{
	@Override public String name() { return "PlayerDelete"; }
	@Override public boolean isAdminMacro()	{return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		if(!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
			return CMProps.getVar(CMProps.Str.MUDSTATUS);

		final String last=httpReq.getUrlParameter("PLAYER");
		if(last==null) return " @break@";
		final MOB M=CMLib.players().getLoadPlayer(last);
		if(M==null) return " @break@";

		CMLib.players().obliteratePlayer(M,true,true);
		Log.sysOut("PlayerDelete","Someone destroyed the user "+M.Name()+".");
		return "";
	}
}
