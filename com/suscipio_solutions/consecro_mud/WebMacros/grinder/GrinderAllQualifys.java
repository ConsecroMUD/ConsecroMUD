package com.suscipio_solutions.consecro_mud.WebMacros.grinder;

import java.util.Map;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.AbilityMapper;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class GrinderAllQualifys
{
	public String name() { return "GrinderAllQualifys"; }

	public String editAllQualify(HTTPRequest httpReq, java.util.Map<String,String> parms)
	{
		final String last=httpReq.getUrlParameter("ALLQUALID");
		if((last==null)||(last.length()==0))
			return " @break@";
		String which=httpReq.getUrlParameter("ALLQUALWHICH");
		if(parms.containsKey("WHICH"))
			which=parms.get("WHICH");
		if((which==null)||(which.length()==0))
			return " @break@";
		final Map<String,Map<String,AbilityMapper.AbilityMapping>> allQualMap=CMLib.ableMapper().getAllQualifiesMap(httpReq.getRequestObjects());
		final Map<String,AbilityMapper.AbilityMapping> map=allQualMap.get(which.toUpperCase().trim());
		if(map==null) return " @break@";

		AbilityMapper.AbilityMapping newMap=map.get(last.toUpperCase().trim());
		if(newMap==null)
		{
			newMap=new AbilityMapper.AbilityMapping(last.toUpperCase().trim());
			newMap.abilityID=last;
			newMap.allQualifyFlag=true;
		}
		String s;
		s=httpReq.getUrlParameter("LEVEL");
		if(s!=null) newMap.qualLevel=CMath.s_int(s);
		s=httpReq.getUrlParameter("PROF");
		if(s!=null) newMap.defaultProficiency=CMath.s_int(s);
		s=httpReq.getUrlParameter("MASK");
		if(s!=null) newMap.extraMask=s;
		s=httpReq.getUrlParameter("AUTOGAIN");
		if(s!=null) newMap.autoGain=s.equalsIgnoreCase("on");
		final StringBuilder preReqs=new StringBuilder("");
		int curChkNum=1;
		while(httpReq.isUrlParameter("REQABLE"+curChkNum))
		{
			final String curVal=httpReq.getUrlParameter("REQABLE"+curChkNum);
			if(curVal.equals("DEL")||curVal.equals("DELETE")||curVal.trim().length()==0)
			{
				// do nothing
			}
			else
			{
				final String curLvl=httpReq.getUrlParameter("REQLEVEL"+curChkNum);
				preReqs.append(curVal);
				if((curLvl!=null)&&(curLvl.trim().length()>0)&&(CMath.s_int(curLvl.trim())>0))
					preReqs.append("(").append(curLvl).append(")");
				preReqs.append(" ");
			}
			curChkNum++;
		}
		newMap=CMLib.ableMapper().makeAbilityMapping(newMap.abilityID,newMap.qualLevel,newMap.abilityID,newMap.defaultProficiency,100,"",newMap.autoGain,false,
										 			 true,CMParms.parseSpaces(preReqs.toString().trim(), true), newMap.extraMask,null);
		map.put(last.toUpperCase().trim(),newMap);
		CMLib.ableMapper().saveAllQualifysFile(allQualMap);
		return "";
	}
}
