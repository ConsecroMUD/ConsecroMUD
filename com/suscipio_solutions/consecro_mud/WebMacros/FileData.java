package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMFile;
import com.suscipio_solutions.consecro_mud.core.exceptions.HTTPServerException;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletResponse;


public class FileData extends StdWebMacro
{
	@Override public String name() { return "FileData"; }

	@Override public boolean isAWebPath(){return true;}
	@Override public boolean preferBinary(){return true;}

	@Override
	public void setServletResponse(SimpleServletResponse response, final String filename)
	{
		String file=filename;
		if(file==null) file="FileData";
		final int x=file.lastIndexOf('/');
		if((x>=0)&&(x<file.length()-1))
			file=file.substring(x+1);
		super.setServletResponse(response, file);
		response.setHeader("Content-Disposition", "attachment; filename="+file);
	}

	@Override
	public String getFilename(HTTPRequest httpReq, String filename)
	{
		final String path=httpReq.getUrlParameter("PATH");
		if(path==null) return filename;
		final String file=httpReq.getUrlParameter("FILE");
		if(file==null) return filename;
		return path+"/"+file;
	}

	@Override
	public byte[] runBinaryMacro(HTTPRequest httpReq, String parm) throws HTTPServerException
	{
		final String filename=getFilename(httpReq,"");
		if(filename.length()==0) return null;
		final MOB M = Authenticate.getAuthenticatedMob(httpReq);
		if(M==null) return null;
		final CMFile F=new CMFile(filename,M);
		if((!F.exists())||(!F.canRead())) return null;
		return F.raw();
	}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm) throws HTTPServerException
	{
		return "[Unimplemented string method!]";
	}
}
