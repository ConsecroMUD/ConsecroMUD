package com.suscipio_solutions.consecro_mud.WebMacros.grinder;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerAccount;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class GrinderAccounts
{
	public String name() { return "GrinderAccounts"; }

	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("ACCOUNT");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			final PlayerAccount A=CMLib.players().getLoadAccount(last);
			if(A!=null)
			{
				String newName=A.getAccountName();
				String str=null;
				String err="";
				str=httpReq.getUrlParameter("NAME");
				if((str!=null)&&(!str.equalsIgnoreCase(A.getAccountName())))
				{
					str=CMStrings.capitalizeAndLower(str);
					if(CMLib.players().getLoadAccount(str)==null)
						newName=str;
					else
						err="Account name '"+str+"' already exists";
				}
				str=httpReq.getUrlParameter("EMAIL");
				if(str!=null) A.setEmail(str);
				str=httpReq.getUrlParameter("NOTES");
				if(str!=null) A.setNotes(str);
				str=httpReq.getUrlParameter("EXPIRATION");
				if(str!=null)
				{
					if(str.equalsIgnoreCase("Never"))
						A.setFlag(PlayerAccount.FLAG_NOEXPIRE, true);
					else
					if(!CMLib.time().isValidDateString(str))
						err="Invalid date string given.";
					else
					{
						A.setFlag(PlayerAccount.FLAG_NOEXPIRE, false);
						final Calendar C=CMLib.time().string2Date(str);
						A.setAccountExpiration(C.getTimeInMillis());
					}
				}
				String id="";
				final StringBuffer flags=new StringBuffer("");
				for(int i=0;httpReq.isUrlParameter("FLAG"+id);id=""+(++i))
					flags.append(httpReq.getUrlParameter("FLAG"+id)+",");
				A.setStat("FLAGS",flags.toString());
				if(err.length()>0)
					return err;
				else
				if(!newName.equalsIgnoreCase(A.getAccountName()))
				{
					final Vector<MOB> V=new Vector<MOB>();
					for(final Enumeration<String> es=A.getPlayers();es.hasMoreElements();)
					{
						final String playerName=es.nextElement();
						final MOB playerM=CMLib.players().getLoadPlayer(playerName);
						if((playerM!=null)&&(!CMLib.flags().isInTheGame(playerM,true)))
							V.addElement(playerM);
					}
					CMLib.database().DBDeleteAccount(A);
					A.setAccountName(newName);
					CMLib.database().DBCreateAccount(A);
					for(final MOB playerM : V)
						CMLib.database().DBUpdatePlayerPlayerStats(playerM);
					httpReq.addFakeUrlParameter("ACCOUNT", newName);
				}
				else
				{
					CMLib.database().DBUpdateAccount(A);
				}
			}
		}
		return "";
	}
}
