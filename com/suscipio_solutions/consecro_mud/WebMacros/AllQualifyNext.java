package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Iterator;
import java.util.Map;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.AbilityMapper;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class AllQualifyNext extends StdWebMacro
{
	@Override public String name() { return "AllQualifyNext"; }
	@Override public boolean isAdminMacro()   {return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("ALLQUALID");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("ALLQUALID");
			return "";
		}
		String which=httpReq.getUrlParameter("ALLQUALWHICH");
		if(parms.containsKey("WHICH"))
			which=parms.get("WHICH");
		if((which==null)||(which.length()==0)) which="ALL";
		final Map<String,Map<String,AbilityMapper.AbilityMapping>> allQualMap=CMLib.ableMapper().getAllQualifiesMap(httpReq.getRequestObjects());
		final Map<String,AbilityMapper.AbilityMapping> map=allQualMap.get(which.toUpperCase().trim());
		if(map==null) return " @break@";

		String lastID="";
		String abilityID;
		for(final Iterator<String> i=map.keySet().iterator();i.hasNext();)
		{
			abilityID=i.next();
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!abilityID.equalsIgnoreCase(lastID))))
			{
				httpReq.addFakeUrlParameter("ALLQUALID",abilityID);
				return "";
			}
			lastID=abilityID;
		}
		httpReq.addFakeUrlParameter("ALLQUALID","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
