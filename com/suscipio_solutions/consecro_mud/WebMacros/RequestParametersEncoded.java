package com.suscipio_solutions.consecro_mud.WebMacros;

import java.net.URLEncoder;

import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class RequestParametersEncoded extends StdWebMacro
{
	@Override public String name() { return "RequestParametersEncoded"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{

		final StringBuilder str=new StringBuilder();
		try
		{
			for(final String key : httpReq.getUrlParameters())
			{
				final String value=httpReq.getUrlParameter(key);
				if(str.length()>0)
					str.append("&");
				str.append(URLEncoder.encode(key,"UTF-8")).append("=").append(URLEncoder.encode(value,"UTF-8"));
			}
		}
		catch(final java.io.UnsupportedEncodingException e)
		{
			Log.errOut(name(),e);
		}
		return clearWebMacros(str.toString());
	}
}
