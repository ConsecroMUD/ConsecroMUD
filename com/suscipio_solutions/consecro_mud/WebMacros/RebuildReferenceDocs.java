package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMFile;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.exceptions.HTTPServerException;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class RebuildReferenceDocs extends StdWebMacro
{
	@Override public String name() { return "RebuildReferenceDocs"; }

	@Override public boolean isAWebPath(){return true;}
	@Override public boolean isAdminMacro() { return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm) throws HTTPServerException
	{
		final MOB M = Authenticate.getAuthenticatedMob(httpReq);
		if(M==null)
			return "[Unauthorized]";
		if(!CMSecurity.isASysOp(M))
			return "[Unallowed]";
		final CMFile sourcesF = new CMFile("/web/admin/work",M,CMFile.FLAG_LOGERRORS);
		if((!sourcesF.canRead())||(!sourcesF.isDirectory())||(sourcesF.list().length==0))
			return "[Unsourced]";
		final CMFile[] sourceFiles = sourcesF.listFiles();
		final long[] processStartTime=new long[]{System.currentTimeMillis()};
		final String[] lastFoundMacro=new String[]{""};
		for (final CMFile sf : sourceFiles)
		{
			if(sf.getName().endsWith(".cmvp"))
			{
				final int sfLen=sf.getName().length();
				final CMFile df=new CMFile("/guides/refs/"+sf.getName().substring(0,sfLen-5)+".html",M);
				if(!df.canWrite())
					return "[Unwrittable: "+df.getName()+"]";
				final byte[] savable = CMLib.webMacroFilter().virtualPageFilter(httpReq, httpReq.getRequestObjects(), processStartTime, lastFoundMacro, new StringBuffer(new String(sf.raw()))).toString().getBytes();
				for(int b=0;b<savable.length-5;b++)
					if((savable[b]=='.') &&(savable[b+1]=='c') &&(savable[b+2]=='m') &&(savable[b+3]=='v') &&(savable[b+4]=='p'))
					{ savable[b+1]='h'; savable[b+2]='t'; savable[b+3]='m'; savable[b+4]='l'; b+=4;}
				df.saveRaw(savable);
			}
		}
		return "[Done!]";
	}
}
