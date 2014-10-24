package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMFile;
import com.suscipio_solutions.consecro_mud.core.Resources;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings("unchecked")
public class RandomAreaTemplates extends StdWebMacro
{
	@Override public String name() { return "RandomAreaTemplates"; }
	@Override public boolean isAdminMacro()	{return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final MOB M = Authenticate.getAuthenticatedMob(httpReq);
		if(M==null) return "[authentication error]";
		try
		{
			final String last=httpReq.getUrlParameter("RTEMPLATE");
			if(parms.containsKey("NEXT"))
			{
				if(parms.containsKey("RESET"))
				{
					if(last!=null) httpReq.removeUrlParameter("RTEMPLATE");
					return "";
				}
				if(last==null) return " @break@";
				List<String> fileList=(List<String>)httpReq.getRequestObjects().get("RANDOMAREATEMPLATESLIST");
				if(fileList==null)
				{
					fileList=new ArrayList<String>();
					final List<String> templateDirs=new LinkedList<String>();
					templateDirs.add("");
					while(templateDirs.size()>0)
					{
						final String templateDirPath=templateDirs.remove(0);
						final CMFile templateDir=new CMFile(Resources.buildResourcePath("randareas/"+templateDirPath),M);
						for(final CMFile file : templateDir.listFiles())
						{
							if(file.isDirectory() && file.canRead())
								templateDirs.add(templateDirPath+file.getName()+"/");
							else
								fileList.add(templateDirPath+file.getName());
						}
					}
					httpReq.getRequestObjects().put("RANDOMAREATEMPLATESLIST", fileList);
				}
				String lastID="";
				for (final String RC : fileList)
				{
					if((last.length()>0)&&(last.equals(lastID))&&(!RC.equals(lastID)))
					{
						httpReq.addFakeUrlParameter("RTEMPLATE",RC);
						return "";
					}
					lastID=RC;
				}
				httpReq.addFakeUrlParameter("RTEMPLATE","");
				if(parms.containsKey("EMPTYOK"))
					return "<!--EMPTY-->";
				return " @break@";
			}
		}
		catch(final Exception e)
		{
			return "[an error occurred performing the last operation]";
		}
		return "";
	}
}
