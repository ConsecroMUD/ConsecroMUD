package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_web.http.HTTPException;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class AddRandomFile extends StdWebMacro
{
	@Override public String name() { return "AddRandomFile"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		if((parms==null)||(parms.size()==0)) return "";
		final StringBuffer buf=new StringBuffer("");
		final int d=CMLib.dice().roll(1,parms.size(),0);
		String file=null;
		int i=0;
		boolean LINKONLY=false;
		for(final String val : parms.values())
			if(val.equalsIgnoreCase("LINKONLY"))
				LINKONLY=true;
		for(final String val : parms.values())
		{
			file=val;
			if(file.equalsIgnoreCase("LINKONLY")) continue;
			if((++i)==d) break;
		}
		if((file!=null)&&(file.length()>0))
		{
			try
			{
				if(LINKONLY)
					buf.append(file);
				else
					buf.append(new String(getHTTPFileData(httpReq,file)));
			}
			catch(final HTTPException e)
			{
				Log.warnOut("Failed "+name()+" "+file);
			}
		}
		return buf.toString();
	}
}
