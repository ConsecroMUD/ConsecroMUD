package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class RoomID extends StdWebMacro
{
	@Override public String name() { return "RoomID"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("ROOM");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			final Room R=CMLib.map().getRoom(last);
			if(R!=null)
				return clearWebMacros(R.roomID());
		}
		return "";
	}
}
