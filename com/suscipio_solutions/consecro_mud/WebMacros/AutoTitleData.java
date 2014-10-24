package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMFile;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.Resources;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class AutoTitleData extends StdWebMacro
{
	@Override public String name() { return "AutoTitleData"; }

	public String deleteTitle(String title)
	{
		CMLib.titles().dispossesTitle(title);
		final CMFile F=new CMFile(Resources.makeFileResourceName("titles.txt"),null,CMFile.FLAG_LOGERRORS);
		if(F.exists())
		{
			final boolean removed=Resources.findRemoveProperty(F, title);
			if(removed)
			{
				Resources.removeResource("titles.txt");
				CMLib.titles().reloadAutoTitles();
				return null;
			}
			return "Unable to delete title!";
		}
		return "Unable to open titles.txt!";
	}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("AUTOTITLE");
		if((last==null)&&(!parms.containsKey("EDIT"))) return " @break@";

		if(parms.containsKey("EDIT"))
		{
			final MOB M = Authenticate.getAuthenticatedMob(httpReq);
			if(M==null) return "[authentication error]";
			if(!CMSecurity.isAllowed(M,M.location(),CMSecurity.SecFlag.TITLES)) return "[authentication error]";
			final String req=httpReq.getUrlParameter("ISREQUIRED");
			String newTitle=httpReq.getUrlParameter("TITLE");
			if((req!=null)&&(req.equalsIgnoreCase("on")))
				newTitle="{"+newTitle+"}";
			final String newMask=httpReq.getUrlParameter("MASK");
			if((newTitle==null)||(newMask==null)||(newTitle.length()==0))
				return "[missing data error]";

			if((last!=null)&&((last.length()==0)&&(CMLib.titles().isExistingAutoTitle(newTitle))))
			{
				CMLib.titles().reloadAutoTitles();
				return "[new title already exists!]";
			}

			final String error=CMLib.titles().evaluateAutoTitle(newTitle+"="+newMask,false);
			if(error!=null) return "[error: "+error+"]";

			if((last!=null)&&(CMLib.titles().isExistingAutoTitle(last)))
			{
				final String err=deleteTitle(last);
				if(err!=null)
				{
					CMLib.titles().reloadAutoTitles();
					return err;
				}
			}
			final CMFile F=new CMFile(Resources.makeFileResourceName("titles.txt"),null,CMFile.FLAG_LOGERRORS);
			F.saveText("\n"+newTitle+"="+newMask,true);
			Resources.removeResource("titles.txt");
			CMLib.titles().reloadAutoTitles();
		}
		else
		if(parms.containsKey("DELETE"))
		{
			final MOB M = Authenticate.getAuthenticatedMob(httpReq);
			if(M==null) return "[authentication error]";
			if(!CMSecurity.isAllowed(M,M.location(),CMSecurity.SecFlag.TITLES)) return "[authentication error]";
			if(last==null) return " @break@";
			if(!CMLib.titles().isExistingAutoTitle(last))
				return "Unknown title!";
			final String err=deleteTitle(last);
			if(err==null) return "Auto-Title deleted.";
			return err;
		}
		else
		if(last==null) return " @break@";
		final StringBuffer str=new StringBuffer("");

		if(parms.containsKey("MASK"))
		{
			String mask=httpReq.getUrlParameter("MASK");
			if((mask==null)&&(last!=null)&&(last.length()>0))
				mask=CMLib.titles().getAutoTitleMask(last);
			if(mask!=null)
				str.append(CMStrings.replaceAll(mask,"\"","&quot;")+", ");
		}
		if(parms.containsKey("TITLE"))
		{
			String title=httpReq.getUrlParameter("TITLE");
			if(title==null)
				title=last;
			if(title!=null)
			{
				if(title.startsWith("{")&&title.endsWith("}"))
					title=title.substring(1,title.length()-1);
				str.append(title+", ");
			}
		}
		if(parms.containsKey("ISREQUIRED"))
		{
			String req=httpReq.getUrlParameter("ISREQUIRED");
			if((req==null)&&(last!=null))
				req=(last.startsWith("{")&&last.endsWith("}"))?"on":"";
			if(req!=null)
				str.append((req.equalsIgnoreCase("on")?"CHECKED":"")+", ");
		}
		if(parms.containsKey("MASKDESC"))
		{
			String mask=httpReq.getUrlParameter("MASK");
			if((mask==null)&&(last!=null)&&(last.length()>0))
				mask=CMLib.titles().getAutoTitleMask(last);
			if(mask!=null)
				str.append(CMLib.masking().maskDesc(mask)+", ");
		}
		String strstr=str.toString();
		if(strstr.endsWith(", "))
			strstr=strstr.substring(0,strstr.length()-2);
		return clearWebMacros(strstr);
	}
}
