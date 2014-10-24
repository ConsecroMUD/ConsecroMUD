package com.suscipio_solutions.consecro_mud.WebMacros;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerAccount;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings({"unchecked","rawtypes"})
public class AccountData extends StdWebMacro
{
	@Override public String name() { return "AccountData"; }
	@Override public boolean isAdminMacro()	{return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("ACCOUNT");
		if(last==null) return "";
		if(last.length()>0)
		{
			final PlayerAccount A = CMLib.players().getLoadAccount(last);
			if(A==null) return "";
			if(parms.containsKey("NAME")||parms.containsKey("ACCOUNT"))
				return clearWebMacros(A.getAccountName());
			if(parms.containsKey("CLASS"))
				return clearWebMacros(A.ID());
			if(parms.containsKey("LASTIP"))
				return ""+A.getLastIP();
			if(parms.containsKey("LASTDATETIME"))
				return ""+CMLib.time().date2String(A.getLastDateTime());
			if(parms.containsKey("EMAIL"))
				return ""+A.getEmail();
			if(parms.containsKey("NOTES"))
				return ""+A.getNotes();
			if(parms.containsKey("ACCTEXPIRATION"))
			{
				if(A.isSet(PlayerAccount.FLAG_NOEXPIRE))
					return "Never";
				return ""+CMLib.time().date2String(A.getAccountExpiration());
			}
			for(final String flag : PlayerAccount.FLAG_DESCS)
				if(parms.containsKey("IS"+flag))
					return ""+A.isSet(flag);
			if(parms.containsKey("FLAGS"))
			{
				final String old=httpReq.getUrlParameter("FLAGS");
				List<String> set=null;
				if(old==null)
				{
					final String matList=A.getStat("FLAG");
					set=CMParms.parseCommas(matList,true);
				}
				else
				{
					String id="";
					set=new Vector();
					for(int i=0;httpReq.isUrlParameter("FLAG"+id);id=""+(++i))
						set.add(httpReq.getUrlParameter("FLAG"+id));
				}
				final StringBuffer str=new StringBuffer("");
				for (final String element : PlayerAccount.FLAG_DESCS)
				{
					str.append("<OPTION VALUE=\""+element+"\"");
					if(set.contains(element)) str.append(" SELECTED");
					str.append(">"+CMStrings.capitalizeAndLower(element));
				}
				str.append(", ");
			}
			if(parms.containsKey("IGNORE"))
				return ""+CMParms.toStringList(A.getIgnored());
		}
		return "";
	}
}
