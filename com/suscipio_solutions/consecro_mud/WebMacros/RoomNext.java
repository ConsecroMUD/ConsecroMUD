package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings("rawtypes")
public class RoomNext extends StdWebMacro
{
	@Override public String name() { return "RoomNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String area=httpReq.getUrlParameter("AREA");
		if((area==null)||(CMLib.map().getArea(area)==null))
			return " @break@";
		final Area A=CMLib.map().getArea(area);
		final String last=httpReq.getUrlParameter("ROOM");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("ROOM");
			return "";
		}
		String lastID="";

		for(final Enumeration d=A.getProperRoomnumbers().getRoomIDs();d.hasMoreElements();)
		{
			final String roomid=(String)d.nextElement();
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!roomid.equals(lastID))))
			{
				httpReq.addFakeUrlParameter("ROOM",roomid);
				return "";
			}
			lastID=roomid;
		}
		httpReq.addFakeUrlParameter("ROOM","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
