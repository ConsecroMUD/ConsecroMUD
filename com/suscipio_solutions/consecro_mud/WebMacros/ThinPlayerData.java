package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.PlayerLibrary;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings("rawtypes")
public class ThinPlayerData extends StdWebMacro {

	@Override public String name() { return "ThinPlayerData"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		if(!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
			return CMProps.getVar(CMProps.Str.MUDSTATUS);

		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("PLAYER");
		if(last==null) return " @break@";
		final StringBuffer str=new StringBuffer("");
		if(last.length()>0)
		{
			String sort=httpReq.getUrlParameter("SORTBY");
			if(sort==null) sort="";
			PlayerLibrary.ThinPlayer player = null;
			final Enumeration pe=CMLib.players().thinPlayers(sort, httpReq.getRequestObjects());
			for(;pe.hasMoreElements();)
			{
				final PlayerLibrary.ThinPlayer TP=(PlayerLibrary.ThinPlayer)pe.nextElement();
				if(TP.name.equalsIgnoreCase(last))
				{
					player = TP;
					break;
				}
			}
			if(player == null) return " @break@";
			for(final String key : parms.keySet())
			{
				final int x=CMLib.players().getCharThinSortCode(key.toUpperCase().trim(),false);
				if(x>=0)
				{
					String value = CMLib.players().getThinSortValue(player, x);
					if(PlayerLibrary.CHAR_THIN_SORT_CODES[x].equals("LAST"))
						value=CMLib.time().date2String(CMath.s_long(value));
					str.append(value+", ");
				}
			}
		}
		String strstr=str.toString();
		if(strstr.endsWith(", "))
			strstr=strstr.substring(0,strstr.length()-2);
		return clearWebMacros(strstr);
	}

}
