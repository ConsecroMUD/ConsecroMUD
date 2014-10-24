package com.suscipio_solutions.consecro_mud.WebMacros;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class AbilityTypeNext extends StdWebMacro
{
	@Override public String name() { return "AbilityTypeNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("ABILITYTYPE");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("ABILITYTYPE");
			return "";
		}
		String lastID="";
		for (final String element : Ability.ACODE_DESCS)
		{
			final String S=element;
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!S.equals(lastID))))
			{
				httpReq.addFakeUrlParameter("ABILITYTYPE",S);
				return "";
			}
			lastID=S;
		}
		httpReq.addFakeUrlParameter("ABILITYTYPE","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
