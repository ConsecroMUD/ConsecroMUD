package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings({"unchecked","rawtypes"})
public class CheckAuthCode extends StdWebMacro
{
	@Override public String name() { return "CheckAuthCode"; }

	public Hashtable getAuths(HTTPRequest httpReq)
	{
		if(!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
			return null;
		final MOB mob = Authenticate.getAuthenticatedMob(httpReq);
		if(mob==null) return null;
		Hashtable auths=(Hashtable)httpReq.getRequestObjects().get("AUTHS_"+mob.Name().toUpperCase().trim());
		if(auths==null)
		{
			auths=new Hashtable();
			boolean subOp=false;
			final boolean sysop=CMSecurity.isASysOp(mob);

			final String AREA=httpReq.getUrlParameter("AREA");
			Room R=null;
			for(final Enumeration a=CMLib.map().areas();a.hasMoreElements();)
			{
				final Area A=(Area)a.nextElement();
				if((AREA==null)||(AREA.length()==0)||(AREA.equals(A.Name())))
					if(A.amISubOp(mob.Name()))
					{
						R=A.getRandomProperRoom();
						subOp=true;
						break;
					}
			}
			auths.put("ANYMODAREAS",""+((subOp&&(CMSecurity.isAllowedAnywhere(mob,CMSecurity.SecFlag.CMDROOMS)||CMSecurity.isAllowedAnywhere(mob,CMSecurity.SecFlag.CMDAREAS)))
														   ||CMSecurity.isAllowedEverywhere(mob,CMSecurity.SecFlag.CMDROOMS)||CMSecurity.isAllowedEverywhere(mob,CMSecurity.SecFlag.CMDAREAS)));
			auths.put("ALLMODAREAS",""+(CMSecurity.isAllowedEverywhere(mob,CMSecurity.SecFlag.CMDROOMS)||CMSecurity.isAllowedEverywhere(mob,CMSecurity.SecFlag.CMDAREAS)));
			final List<String> dirs=CMSecurity.getAccessibleDirs(mob,mob.location());
			auths.put("ANYFILEBROWSE",""+(dirs.size()>0));
			if(dirs.size()>0)
			{
				int maxLen=Integer.MAX_VALUE;
				int maxOne=-1;
				for(int v=0;v<dirs.size();v++)
					if(dirs.get(v).length()<maxLen)
					{
						maxLen=dirs.get(v).length();
						maxOne=v;
					}
				final String winner=dirs.get(maxOne);
				httpReq.addFakeUrlParameter("BESTFILEBROWSE",winner);
			}
			else
				httpReq.addFakeUrlParameter("BESTFILEBROWSE","");
			auths.put("SYSOP",""+sysop);
			auths.put("SUBOP",""+(sysop||subOp));

			for(final Iterator<CMSecurity.SecFlag> i = CMSecurity.getSecurityCodes(mob,R);i.hasNext();)
				auths.put("AUTH_"+i.next().toString(),"true");
			httpReq.getRequestObjects().put("AUTHS_"+mob.Name().toUpperCase().trim(),auths);
		}
		return auths;
	}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		boolean finalCondition=false;
		final Hashtable auths=getAuths(httpReq);
		if(auths==null) return "false";
		final boolean sysop=((String)auths.get("SYSOP")).equalsIgnoreCase("true");
		for(String key : parms.keySet())
		{
			final String equals=parms.get(key);
			boolean not=false;
			boolean thisCondition=true;
			if(key.startsWith("||")) key=key.substring(2);
			if(key.startsWith("!"))
			{
				key=key.substring(1);
				not=true;
			}
			final String check=sysop?"true":(String)auths.get(key);
			if(not)
			{
				if((check==null)&&(equals.length()==0))
					thisCondition=false;
				else
				if(check==null)
					thisCondition=true;
				else
				if(!check.equalsIgnoreCase(equals))
					thisCondition=true;
				else
					thisCondition=false;
			}
			else
			{
				if((check==null)&&(equals.length()==0))
					thisCondition=true;
				else
				if(check==null)
					thisCondition=false;
				else
				if(!check.equalsIgnoreCase(equals))
					thisCondition=false;
				else
					thisCondition=true;
			}
			finalCondition=finalCondition||thisCondition;
		}
		if(finalCondition)
			return "true";
		return "false";
	}
}
