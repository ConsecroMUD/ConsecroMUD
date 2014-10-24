package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.exceptions.HTTPServerException;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletResponse;


public class AreaXML extends StdWebMacro
{
	@Override public String name() { return "AreaXML"; }

	@Override public boolean isAWebPath(){return true;}
	@Override public boolean preferBinary(){return true;}

	@Override
	public void setServletResponse(SimpleServletResponse response, final String filename)
	{
		response.setHeader("Content-Disposition", "attachment; filename="+filename);
		response.setHeader("Content-Type", "application/cmare");
	}

	@Override
	public String getFilename(HTTPRequest httpReq, String filename)
	{
		final MOB mob = Authenticate.getAuthenticatedMob(httpReq);
		if(mob==null) return "area.xml";
		final Area pickedA=getLoggedArea(httpReq,mob);
		if(pickedA==null) return "area.xml";
		String fileName="";
		if(pickedA.getArchivePath().length()>0)
			fileName=pickedA.getArchivePath();
		else
			fileName=pickedA.Name();
		if(fileName.indexOf('.')<0)
			fileName=fileName+".cmare";
		return fileName;
	}

	protected Area getLoggedArea(HTTPRequest httpReq, MOB mob)
	{
		final String AREA=httpReq.getUrlParameter("AREA");
		if(AREA==null) return null;
		if(AREA.length()==0) return null;
		final Area A=CMLib.map().getArea(AREA);
		if(A==null) return null;
		if(CMSecurity.isASysOp(mob)||A.amISubOp(mob.Name()))
			return A;
		return null;
	}

	@Override
	public byte[] runBinaryMacro(HTTPRequest httpReq, String parm) throws HTTPServerException
	{
		final MOB mob = Authenticate.getAuthenticatedMob(httpReq);
		if(mob==null) return null;
		final Area pickedA=getLoggedArea(httpReq,mob);
		if(pickedA==null) return null;
		final Command C=CMClass.getCommand("Export");
		if(C==null) return null;
		Object resultO=null;
		try
		{
			resultO=C.executeInternal(mob,0,"AREA","DATA","MEMORY",Integer.valueOf(4),null,pickedA,mob.location());
		}
		catch(final Exception e)
		{
			return null;
		}
		if(!(resultO instanceof String))
			return null;
		return ((String)resultO).getBytes();
	}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm) throws HTTPServerException
	{
		return "[Unimplemented string method!]";
	}
}
