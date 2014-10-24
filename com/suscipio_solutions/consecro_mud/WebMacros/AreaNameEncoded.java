package com.suscipio_solutions.consecro_mud.WebMacros;

import java.net.URLEncoder;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;




public class AreaNameEncoded extends StdWebMacro
{
	@Override public String name() { return "AreaNameEncoded"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("AREA");
		if(last==null) return "";
		if(last.length()>0)
		{
			final Area A=CMLib.map().getArea(last);
			if(A!=null)
			{
				try
				{
					return clearWebMacros(URLEncoder.encode(A.Name(),"UTF-8"));
				}
				catch(final java.io.UnsupportedEncodingException e)
				{
					Log.errOut(name(),"Wrong Encoding");
				}
			}
		}
		return "";
	}
}
