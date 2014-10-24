package com.suscipio_solutions.consecro_mud.WebMacros;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class AbilityName extends StdWebMacro
{
	@Override public String name() { return "AbilityName"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("ABILITY");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			final Ability A=CMClass.getAbility(last);
			if(A!=null)
				return clearWebMacros(A.Name());
		}
		return "";
	}
}
