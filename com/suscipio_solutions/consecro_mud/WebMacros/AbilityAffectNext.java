package com.suscipio_solutions.consecro_mud.WebMacros;
import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class AbilityAffectNext extends StdWebMacro
{
	@Override public String name() { return "AbilityAffectNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("ABILITY");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("ABILITY");
			return "";
		}
		String lastID="";
		final String ableType=httpReq.getUrlParameter("ABILITYTYPE");
		if((ableType!=null)&&(ableType.length()>0))
			parms.put(ableType,ableType);
		for(final Enumeration<Ability> a=CMClass.abilities();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			boolean okToShow=true;
			final int classType=A.classificationCode()&Ability.ALL_ACODES;
			if(CMLib.ableMapper().getQualifyingLevel("Immortal",true,A.ID())>=0)
				continue;
			boolean containsOne=false;
			for (final String element : Ability.ACODE_DESCS)
				if(parms.containsKey(element))
				{ containsOne=true; break;}
			if(containsOne&&(!parms.containsKey(Ability.ACODE_DESCS[classType])))
				okToShow=false;
			if(parms.containsKey("NOT")) okToShow=!okToShow;
			if(okToShow)
			{
				if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!A.ID().equals(lastID))))
				{
					httpReq.addFakeUrlParameter("ABILITY",A.ID());
					return "";
				}
				lastID=A.ID();
			}
		}
		httpReq.addFakeUrlParameter("ABILITY","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
