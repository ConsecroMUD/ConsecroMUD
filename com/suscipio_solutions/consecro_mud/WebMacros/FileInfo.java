package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMFile;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_web.http.MIMEType;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class FileInfo extends StdWebMacro
{
	@Override public String name() { return "FileInfo"; }
	@Override public boolean isAdminMacro()	{return true;}

	public String trimSlash(String path)
	{
		path=path.trim();
		while(path.startsWith("/"))
			path=path.substring(1);
		while(path.endsWith("/"))
			path=path.substring(0,path.length()-1);
		return path;
	}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		String path=httpReq.getUrlParameter("PATH");
		if(path==null) path="";
		String file=httpReq.getUrlParameter("FILE");
		if(file==null) file="";
		final MOB M = Authenticate.getAuthenticatedMob(httpReq);
		if(M==null) return "[authentication error]";
		try
		{
			final String filepath=path.endsWith("/")?path+file:path+"/"+file;
			final String pathKey="CMFSFILE_"+trimSlash(filepath);
			CMFile F=(CMFile)httpReq.getRequestObjects().get(pathKey);
			if(F==null)
			{
				F=new CMFile(filepath,M);
				httpReq.getRequestObjects().put(pathKey, F);
			}
			if(parms.containsKey("ISDIRECTORY"))
				return ""+F.isDirectory();
			if(parms.containsKey("ISFILE"))
				return ""+F.isFile();
			if(parms.containsKey("ISLOCAL"))
				return ""+F.canLocalEquiv();
			if(parms.containsKey("ISBOTH"))
				return ""+(F.canLocalEquiv()&&(F.canVFSEquiv()));
			if(parms.containsKey("ISVFS"))
				return ""+F.canVFSEquiv();
			if(parms.containsKey("ISTEXT"))
			{
				final int x=F.getName().lastIndexOf('.');
				if(x<0) return "false";
				final String mime=MIMEType.getMIMEType(F.getName().substring(x)).getType();
				if(mime.toUpperCase().startsWith("TEXT"))
					return "true";
				return "false";
			}
			if(parms.containsKey("ISBINARY"))
			{
				final int x=F.getName().lastIndexOf('.');
				if(x<0) return "true";
				final String mime=MIMEType.getMIMEType(F.getName().substring(x)).getType();
				if(mime.toUpperCase().startsWith("TEXT"))
					return "false";
				return "true";
			}
			if(parms.containsKey("NAME"))
				return ""+F.getName();
			if(parms.containsKey("DATA"))
				return F.textUnformatted().toString();
			if(parms.containsKey("TEXTDATA"))
			{
				String s=F.text().toString();
				s=CMStrings.replaceAll(s,"\n\r","\n");
				s=CMStrings.replaceAll(s,"&","&amp;");
				s=CMStrings.replaceAll(s,"@","&#64;");
				s=CMStrings.replaceAll(s,"<","&lt;");
				return s;
			}
		}
		catch(final Exception e)
		{
			return "[error]";
		}
		return "";
	}
}
