package com.suscipio_solutions.consecro_mud.WebMacros;

import java.net.URLEncoder;

import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class RequestParameterEncoded extends RequestParameter
{
	@Override public String name() { return "RequestParameterEncoded"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{

		String str=super.runMacro(httpReq, parm);
		try
		{
			str=URLEncoder.encode(str,"UTF-8");
		}
		catch(final java.io.UnsupportedEncodingException ex)
		{
			Log.errOut(name(),"Wrong Encoding");
		}
		return str;
	}
}
