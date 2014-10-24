package com.suscipio_solutions.consecro_mud.WebMacros;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.collections.PairSVector;
import com.suscipio_solutions.consecro_web.http.HTTPException;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class AddFile extends StdWebMacro
{
	@Override public String name() { return "AddFile"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final PairSVector<String,String> parms=super.parseOrderedParms(parm,true);
		if((parms==null)||(parms.size()==0)) return "";
		final StringBuffer buf=new StringBuffer("");
		boolean webify=false;
		boolean replace=false;
		for(final Pair<String,String> p : parms)
		{
			final String key=p.first;
			final String file = p.second;
			if(file.length()>0)
			{
				try
				{
					if(key.trim().equalsIgnoreCase("webify"))
						webify=true;
					else
					if(key.trim().equalsIgnoreCase("replace"))
						replace=true;
					else
					if(replace)
					{
						int x=buf.indexOf(key);
						while(x>=0)
						{
							if(webify)
								buf.replace(x,x+key.length(),webify(new StringBuffer(file)).toString());
							else
								buf.replace(x,x+key.length(),file);
							x=buf.indexOf(key,x+key.length());
						}
					}
					else
					if(webify)
						buf.append(webify(new StringBuffer(new String(getHTTPFileData(httpReq,file.trim())))));
					else
						buf.append(new String(getHTTPFileData(httpReq,file.trim())));
				}
				catch(final HTTPException e)
				{
					Log.warnOut("Failed "+name()+" "+file);
				}
			}
		}
		return buf.toString();
	}
}
