package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings({"unchecked","rawtypes"})
public class INIValue extends StdWebMacro
{
	@Override public String name() { return "INIValue"; }
	@Override public boolean isAdminMacro()	{return true;}


	public String getHelpFor(String tag, String mask)
	{
		final Vector help=new Vector();
		final List<String> page=CMProps.loadEnumerablePage(CMProps.getVar(CMProps.Str.INIPATH));
		boolean startOver=false;
		for(int p=0;p<page.size();p++)
		{
			final String s=page.get(p).trim();
			if(s.trim().length()==0)
				startOver=true;
			else
			if(s.startsWith("#")||s.startsWith("!"))
			{
				if(startOver) help.clear();
				startOver=false;
				help.addElement(s.substring(1).trim());
			}
			else
			{
				final int x=s.indexOf('=');
				if((x>=0)
				&&(help.size()>0)
				&&((s.substring(0,x).equals(mask)
					||(mask.endsWith("*")&&(s.substring(0,x).startsWith(mask.substring(0,mask.length()-1)))))))
				{
					final StringBuffer str=new StringBuffer("");
					for(int i=0;i<help.size();i++)
						str.append(((String)help.elementAt(i))+"<BR>");
					return str.toString();
				}
				help.clear();
				startOver=false;
			}
		}
		return "";
	}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		if(parms==null) return "";
		final String last=httpReq.getUrlParameter("INI");
		if((parms.size()==0)&&(last!=null)&&(last.length()>0))
		{
			final CMProps page=CMProps.loadPropPage(CMProps.getVar(CMProps.Str.INIPATH));
			if((page==null)||(!page.isLoaded())) return "";
			return page.getStr(last);
		}
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("INI");
			return "";
		}
		if(parms.containsKey("NEXT"))
		{
			if(!parms.containsKey("MASK"))
				return " @break@";
			final String mask=parms.get("MASK").toUpperCase().trim();
			String lastID="";
			final List<String> page=CMProps.loadEnumerablePage(CMProps.getVar(CMProps.Str.INIPATH));
			for(int p=0;p<page.size();p++)
			{
				final String s=page.get(p).trim();
				if(s.startsWith("#")||s.startsWith("!"))
					continue;
				int x=s.indexOf('=');
				if(x<0) x=s.indexOf(':');
				if(x<0) continue;
				final String id=s.substring(0,x).trim().toUpperCase();
				if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!id.equals(lastID))))
				{
					if(mask.endsWith("*"))
					{
						if(!id.startsWith(mask.substring(0,mask.length()-1)))
							continue;
					}
					else
					if(!mask.equalsIgnoreCase(id))
						continue;
					httpReq.addFakeUrlParameter("INI",id);
					if(parms.containsKey("VALUE"))
					{
						final CMProps realPage=CMProps.loadPropPage(CMProps.getVar(CMProps.Str.INIPATH));
						if(realPage!=null) return realPage.getStr(id);
					}
					return "";
				}
				lastID=id;
			}
			httpReq.addFakeUrlParameter("INI","");
			if(parms.containsKey("EMPTYOK"))
				return "<!--EMPTY-->";
			return " @break@";
		}
		if(!parms.containsKey("MASK"))
			return "'MASK' not found!";
		final String mask=parms.get("MASK").toUpperCase();
		final CMProps page=CMProps.loadPropPage(CMProps.getVar(CMProps.Str.INIPATH));
		if((page==null)||(!page.isLoaded())) return "";
		if(mask.trim().endsWith("*"))
			for(final Enumeration e=page.keys();e.hasMoreElements();)
			{
				final String key=((String)e.nextElement()).toUpperCase();
				if(key.startsWith(mask.substring(0,mask.length()-1)))
				{
					httpReq.addFakeUrlParameter("INI",key);
					if(parms.containsKey("VALUE"))
						return clearWebMacros(page.getStr(key));
					else
					if(parms.containsKey("INIHELP"))
						return clearWebMacros(getHelpFor(key,mask));
					return "";
				}
			}
		httpReq.addFakeUrlParameter("INI",mask);
		if(parms.containsKey("VALUE"))
			return clearWebMacros(page.getStr(mask));
		else
		if(parms.containsKey("INIHELP"))
			return clearWebMacros(getHelpFor(mask,mask));
		return "";
	}
}
