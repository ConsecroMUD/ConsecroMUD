package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Resources;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings({"unchecked","rawtypes"})
public class HolidayNext extends StdWebMacro
{
	@Override public String name() { return "HolidayNext"; }
	@Override public boolean isAdminMacro()   {return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("HOLIDAY");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("HOLIDAY");
			return "";
		}
		final Object resp=CMLib.quests().getHolidayFile();
		List<String> steps=null;
		if(resp instanceof List)
			steps=(List<String>)resp;
		else
		if(resp instanceof String)
			return (String)resp;
		else
			return "[Unknown error.]";
		final Vector holidays=new Vector();
		List<String> line=null;
		String var=null;
		List<String> V=null;
		for(int s=1;s<steps.size();s++)
		{
			final String step=steps.get(s);
			V=Resources.getFileLineVector(new StringBuffer(step));
			final List<List<String>> cmds=CMLib.quests().parseQuestCommandLines(V,"SET",0);
			//Vector areaLine=null;
			List<String> nameLine=null;
			for(int v=0;v<cmds.size();v++)
			{
				line=cmds.get(v);
				if(line.size()>1)
				{
					var=line.get(1).toUpperCase();
					//if(var.equals("AREAGROUP"))
					//{ areaLine=line;}
					if(var.equals("NAME"))
					{ nameLine=line;}
				}
			}
			if(nameLine!=null)
			{
				/*String areaName=null;
				if(areaLine==null)
					areaName="*special*";
				else
					areaName=CMParms.combineWithQuotes(areaLine,2);*/
				final String name=CMParms.combine(nameLine,2);
				holidays.addElement(name);
			}
		}
		String lastID="";
		for(final Enumeration q=holidays.elements();q.hasMoreElements();)
		{
			final String holidayID=(String)q.nextElement();
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!holidayID.equalsIgnoreCase(lastID))))
			{
				httpReq.addFakeUrlParameter("HOLIDAY",holidayID);
				return "";
			}
			lastID=holidayID;
		}
		httpReq.addFakeUrlParameter("HOLIDAY","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
