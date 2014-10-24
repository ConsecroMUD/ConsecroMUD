package com.suscipio_solutions.consecro_mud.WebMacros;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings({"unchecked","rawtypes"})
public class AbilityPlayerNext extends StdWebMacro
{
	@Override public String name() { return "AbilityPlayerNext"; }

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
		final String playerName=httpReq.getUrlParameter("PLAYER");
		MOB M=null;
		if((playerName!=null)&&(playerName.length()>0))
			M=CMLib.players().getLoadPlayer(playerName);
		if(M==null)
		{
			if(parms.containsKey("EMPTYOK"))
				return "<!--EMPTY-->";
			return " @break@";
		}

		final Vector abilities=new Vector();
		HashSet foundIDs=new HashSet();
		for(final Enumeration<Ability> a=M.allAbilities();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if((A!=null)&&(!foundIDs.contains(A.ID())))
			{
				foundIDs.add(A.ID());
				abilities.addElement(A);
			}
		}
		foundIDs.clear();
		foundIDs=null;
		for(int a=0;a<abilities.size();a++)
		{
			final Ability A=(Ability)abilities.elementAt(a);
			boolean okToShow=true;
			final int classType=A.classificationCode()&Ability.ALL_ACODES;
			final String className=httpReq.getUrlParameter("CLASS");

			if((className!=null)&&(className.length()>0))
			{
				final int level=CMLib.ableMapper().getQualifyingLevel(className,true,A.ID());
				if(level<0)
					okToShow=false;
				else
				{
					final String levelName=httpReq.getUrlParameter("LEVEL");
					if((levelName!=null)&&(levelName.length()>0)&&(CMath.s_int(levelName)!=level))
						okToShow=false;
				}
			}
			else
			{
				final int level=CMLib.ableMapper().getQualifyingLevel("Immortal",true,A.ID());
				if(level<0)
					okToShow=false;
				else
				{
					final String levelName=httpReq.getUrlParameter("LEVEL");
					if((levelName!=null)&&(levelName.length()>0)&&(CMath.s_int(levelName)!=level))
						okToShow=false;
				}
			}
			if(okToShow)
			{
				if(parms.containsKey("DOMAIN")&&(classType==Ability.ACODE_SPELL))
				{
					final String domain=parms.get("DOMAIN");
					if(!domain.equalsIgnoreCase(Ability.DOMAIN_DESCS[(A.classificationCode()&Ability.ALL_DOMAINS)>>5]))
					   okToShow=false;
				}
				else
				{
					boolean containsOne=false;
					for (final String element : Ability.ACODE_DESCS)
						if(parms.containsKey(element))
						{ containsOne=true; break;}
					if(containsOne&&(!parms.containsKey(Ability.ACODE_DESCS[classType])))
						okToShow=false;
				}
			}
			if(parms.containsKey("NOT"))
				okToShow=!okToShow;
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
