package com.suscipio_solutions.consecro_mud.WebMacros;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.ItemCraftor;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings("rawtypes")
public class AbilityNext extends StdWebMacro
{
	@Override public String name() { return "AbilityNext"; }

	@Override
	@SuppressWarnings("unchecked")
	public String runMacro(HTTPRequest httpReq, String parm)
	{
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
		long flags=0;
		final String flagString=httpReq.getUrlParameter("FLAGS");
		if((flagString!=null)&&(flagString.length()>0))
		{
			final List<String> V=CMParms.parseSquiggles(flagString.toUpperCase());
			for(int i=0;i<Ability.FLAG_DESCS.length;i++)
				if(V.contains(Ability.FLAG_DESCS[i]))
					flags=flags|(CMath.pow(2,i));
		}

		String lastID="";
		final String className=httpReq.getUrlParameter("CLASS");
		final boolean genericOnly =parms.containsKey("GENERIC");
		final boolean parmsEditable=parms.containsKey("PARMSEDITABLE");
		final boolean unqualifiedOK=parms.containsKey("UNQUALIFIEDOK");
		final String levelName=httpReq.getUrlParameter("LEVEL");
		final boolean notFlag =parms.containsKey("NOT");
		final boolean allFlag =parms.containsKey("ALL");
		final boolean domainFlag=parms.containsKey("DOMAIN");
		final String domain=parms.get("DOMAIN");


		final Enumeration<Ability> a;
		if(!parms.containsKey("SORTEDBYNAME"))
			a=CMClass.abilities();
		else
		if(httpReq.getRequestObjects().containsKey("ABILITIESSORTEDBYNAME"))
			a=((Vector)httpReq.getRequestObjects().get("ABILITIESSORTEDBYNAME")).elements();
		else
		{
			final Vector<Ability> fullList=new Vector<Ability>();
			for(final Enumeration<Ability> aa=CMClass.abilities();aa.hasMoreElements();)
				fullList.add(aa.nextElement());
			final Ability[] aaray=fullList.toArray(new Ability[0]);
			Arrays.sort(aaray, new Comparator<Ability>()
			{
				@Override public int compare(Ability o1, Ability o2)
				{
					return o1.Name().compareToIgnoreCase(o2.Name());
				}
			});
			fullList.clear();
			fullList.addAll(Arrays.asList(aaray));
			httpReq.getRequestObjects().put("ABILITIESSORTEDBYNAME",fullList);
			a=fullList.elements();
		}
		for(;a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			boolean okToShow=true;
			final int classType=A.classificationCode()&Ability.ALL_ACODES;
			if(genericOnly)
				okToShow=A.isGeneric();
			else
			if(parmsEditable)
				okToShow=((A instanceof ItemCraftor)
					   &&(((ItemCraftor)A).parametersFile()!=null)
					   &&(((ItemCraftor)A).parametersFile().length()>0)
					   &&(((ItemCraftor)A).parametersFormat()!=null)
					   &&(((ItemCraftor)A).parametersFormat().length()>0));

			if((className!=null)&&(className.length()>0))
			{
				final int level=CMLib.ableMapper().getQualifyingLevel(className,true,A.ID());
				if((level<0)&&(!unqualifiedOK))
					okToShow=false;
				else
				if(CMLib.ableMapper().getSecretSkill(className,false,A.ID()))
					okToShow=false;
				else
				if((flags>0)&&((A.flags()&flags)!=flags))
					okToShow=false;
				else
				{
					if((levelName!=null)&&(levelName.length()>0)&&(CMath.s_int(levelName)!=level))
						okToShow=false;
				}
			}
			else
			if(!allFlag)
			{
				final int level=CMLib.ableMapper().getQualifyingLevel("Immortal",true,A.ID());
				if((level<0)&&(!unqualifiedOK))
					okToShow=false;
				else
				if(CMLib.ableMapper().getAllSecretSkill(A.ID()))
					okToShow=false;
				else
				if((flags>0)&&((A.flags()&flags)!=flags))
					okToShow=false;
				else
				{
					if((levelName!=null)&&(levelName.length()>0)&&(CMath.s_int(levelName)!=level))
						okToShow=false;
				}
			}
			if(okToShow)
			{
				if((domainFlag)&&(!domain.equalsIgnoreCase(Ability.DOMAIN_DESCS[(A.classificationCode()&Ability.ALL_DOMAINS)>>5])))
				   okToShow=false;
				boolean containsOne=false;
				for (final String element : Ability.ACODE_DESCS)
					if(parms.containsKey(element))
					{ containsOne=true; break;}
				if(containsOne&&(!parms.containsKey(Ability.ACODE_DESCS[classType])))
					okToShow=false;
			}
			if(notFlag) okToShow=!okToShow;
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
