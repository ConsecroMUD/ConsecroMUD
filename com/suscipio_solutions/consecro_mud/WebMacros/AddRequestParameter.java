package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class AddRequestParameter extends StdWebMacro
{
	@Override public String name() { return "AddRequestParameter"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String str="";
		final java.util.Map<String,String> parms=parseParms(parm);

		for(final String key : parms.keySet())
		{
			if(key!=null)
			{
				String val=parms.get(key);
				if(val==null) val="";
				if((val.equals("++")&&(httpReq.isUrlParameter(key))))
					val=""+(CMath.s_int(httpReq.getUrlParameter(key))+1);
				else
				if((val.equals("--")&&(httpReq.isUrlParameter(key))))
					val=""+(CMath.s_int(httpReq.getUrlParameter(key))-1);

				httpReq.addFakeUrlParameter(key,val);
			}
		}
		return clearWebMacros(str);
	}
}
