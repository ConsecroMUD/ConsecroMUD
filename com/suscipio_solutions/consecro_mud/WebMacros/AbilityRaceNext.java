package com.suscipio_solutions.consecro_mud.WebMacros;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class AbilityRaceNext extends StdWebMacro
{
	@Override public String name() { return "AbilityRaceNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		if(!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
			return CMProps.getVar(CMProps.Str.MUDSTATUS);

		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("ABILITY");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("ABILITY");
			return "";
		}
		final String ableType=httpReq.getUrlParameter("ABILITYTYPE");
		if((ableType!=null)&&(ableType.length()>0))
			parms.put(ableType,ableType);
		final String domainType=httpReq.getUrlParameter("DOMAIN");
		if((domainType!=null)&&(domainType.length()>0))
			parms.put("DOMAIN",domainType);

		String lastID="";
		final String raceID=httpReq.getUrlParameter("RACE");
		Race R=null;
		if((raceID!=null)&&(raceID.length()>0))
			R=CMClass.getRace(raceID);
		if(R==null)
		{
			if(parms.containsKey("EMPTYOK"))
				return "<!--EMPTY-->";
			return " @break@";
		}

		for(final Ability A : R.racialAbilities(null))
		{
			boolean okToShow=true;
			final int level=CMLib.ableMapper().getQualifyingLevel(R.ID(),false,A.ID());
			if(level<0)
				okToShow=false;
			else
			{
				final String levelName=httpReq.getUrlParameter("LEVEL");
				if((levelName!=null)&&(levelName.length()>0)&&(CMath.s_int(levelName)!=level))
					okToShow=false;
			}
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
