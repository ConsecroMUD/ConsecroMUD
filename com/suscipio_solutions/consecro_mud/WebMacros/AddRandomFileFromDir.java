package com.suscipio_solutions.consecro_mud.WebMacros;

import java.io.File;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_web.http.HTTPException;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings({"unchecked","rawtypes"})
public class AddRandomFileFromDir extends StdWebMacro
{
	@Override public String name() { return "AddRandomFileFromDir"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		if((parms==null)||(parms.size()==0)) return "";
		final StringBuffer buf=new StringBuffer("");
		final Vector fileList=new Vector();
		boolean LINKONLY=false;
		for(final String val : parms.values())
			if(val.equalsIgnoreCase("LINKONLY"))
				LINKONLY=true;
		for(String filePath : parms.values())
		{
			if(filePath.equalsIgnoreCase("LINKONLY")) continue;
			final File directory=grabFile(httpReq,filePath);
			if((!filePath.endsWith("/"))&&(!filePath.endsWith("/")))
				filePath+="/";
			if((directory!=null)&&(directory.canRead())&&(directory.isDirectory()))
			{
				final String[] list=directory.list();
				for (final String element : list)
					fileList.addElement(filePath+element);
			}
			else
				Log.sysOut("AddRFDir","Directory error: "+filePath);
		}
		if(fileList.size()==0)
			return buf.toString();

		try
		{
			if(LINKONLY)
				buf.append((String)fileList.elementAt(CMLib.dice().roll(1,fileList.size(),-1)));
			else
				buf.append(new String(getHTTPFileData(httpReq,(String)fileList.elementAt(CMLib.dice().roll(1,fileList.size(),-1)))));
		}
		catch(final HTTPException e)
		{
			Log.warnOut("Failed "+name()+" "+(String)fileList.elementAt(CMLib.dice().roll(1,fileList.size(),-1)));
		}
		return buf.toString();
	}
}
