package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.HashSet;

import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class RequestParameter extends StdWebMacro
{
	@Override public String name() { return "RequestParameter"; }
	private static enum MODIFIER {UPPERCASE,LOWERCASE,LEFT,RIGHT,ELLIPSE,TRIM,AFTER,CAPITALCASE}
	private static HashSet<String> modifiers=new HashSet<String>();
	static
	{
		for(final MODIFIER M : MODIFIER.values())
			modifiers.add(M.name());
	}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		String str="";
		final java.util.Map<String,String> parms=parseParms(parm);
		for(final String key : parms.keySet())
		{
			if(!modifiers.contains(key))
				if(httpReq.isUrlParameter(key))
					str+=httpReq.getUrlParameter(key);
		}
		boolean capCase=false;
		for(final String key : parms.keySet())
		{
			if(modifiers.contains(key))
			{
				int num = 0;
				if(key.equals(MODIFIER.UPPERCASE.name()))
					str=str.toUpperCase();
				else
				if(key.equals(MODIFIER.LOWERCASE.name()))
					str=str.toLowerCase();
				else
				if(key.equals(MODIFIER.CAPITALCASE.name()))
					capCase=true;
				else
				if(key.equals(MODIFIER.TRIM.name()))
					str=str.trim();
				else
				if(key.equals(MODIFIER.LEFT.name()))
				{
					num = CMath.s_int(parms.get(MODIFIER.LEFT.name()));
					if((num >0)&& (num < str.length()))
						str=str.substring(0,num);
				}
				else
				if(key.equals(MODIFIER.AFTER.name()))
				{
					num = CMath.s_int(parms.get(MODIFIER.AFTER.name()));
					if((num >0)&& (num < str.length()))
						str=str.substring(num);
				}
				else
				if(key.equals(MODIFIER.RIGHT.name()))
				{
					num = CMath.s_int(parms.get(MODIFIER.RIGHT.name()));
					if((num >0)&& (num < str.length()))
						str=str.substring(str.length()-num);
				}
				else
				if(key.equals(MODIFIER.ELLIPSE.name()))
				{
					num = CMath.s_int(parms.get(MODIFIER.ELLIPSE.name()));
					if((num >0)&& (num < str.length()))
						str=str.substring(0,num)+"...";
				}
			}
		}
		if(capCase)
			str=CMStrings.capitalizeAndLower(str);
		str=clearWebMacros(str);
		return str;
	}
}
