package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.List;

import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Resources;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class BanListMgr extends StdWebMacro
{
	@Override public String name() { return "BanListMgr"; }
	@Override public boolean isAdminMacro()	{return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("BANNEDONE");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("BANNEDONE");
			return "";
		}
		else
		if(parms.containsKey("NEXT"))
		{
			String lastID="";
			final List<String> banned=Resources.getFileLineVector(Resources.getFileResource("banned.ini",false));
			for(int i=0;i<banned.size();i++)
			{
				final String key=banned.get(i);
				if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!key.equals(lastID))))
				{
					httpReq.addFakeUrlParameter("BANNEDONE",key);
					return "";
				}
				lastID=key;
			}
			httpReq.addFakeUrlParameter("BANNEDONE","");
			if(parms.containsKey("EMPTYOK"))
				return "<!--EMPTY-->";
			return " @break@";
		}
		else
		if(parms.containsKey("DELETE"))
		{
			final String key=httpReq.getUrlParameter("BANNEDONE");
			if(key==null) return "";
			CMSecurity.unban(key);
			return "'"+key+"' no longer banned.";
		}
		else
		if(parms.containsKey("ADD"))
		{
			final String key=httpReq.getUrlParameter("NEWBANNEDONE");
			if(key==null) return "";
			CMSecurity.ban(key);
			return "'"+key+"' is now banned.";
		}
		else
		if(last!=null)
			return last;
		return "<!--EMPTY-->";
	}

}
