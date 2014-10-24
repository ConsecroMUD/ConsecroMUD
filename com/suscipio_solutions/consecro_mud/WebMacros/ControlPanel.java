package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMSecurity.DbgFlag;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;
import com.suscipio_solutions.consecro_web.util.CWConfig;
import com.suscipio_solutions.consecro_web.util.CWThread;


public class ControlPanel extends StdWebMacro
{
	@Override public String name() { return "ControlPanel"; }
	@Override public boolean isAdminMacro()    {return true;}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);

		final String lastDisable=httpReq.getUrlParameter("DISABLEFLAG");
		if(parms.containsKey("DISABLERESET"))
		{
			if(lastDisable!=null) httpReq.removeUrlParameter("DISABLEFLAG");
			return "";
		}
		if(parms.containsKey("DISABLENEXT"))
		{
			String lastID="";
			for(final CMSecurity.DisFlag flag : CMSecurity.DisFlag.values())
			{
				if((lastDisable==null)||((lastDisable.length()>0)&&(lastDisable.equals(lastID))&&(!flag.toString().equals(lastID))))
				{
					httpReq.addFakeUrlParameter("DISABLEFLAG",flag.toString());
					return "";
				}
				lastID=flag.toString();
			}
			httpReq.addFakeUrlParameter("DISABLEFLAG","");
			if(parms.containsKey("EMPTYOK"))
				return "<!--EMPTY-->";
			return " @break@";

		}
		if(parms.containsKey("DISABLEID"))
		{
			if(lastDisable==null)
				return " @break@";
			return lastDisable;
		}
		if(parms.containsKey("DISABLEDESC"))
		{
			if(lastDisable==null)
				return " @break@";
			final CMSecurity.DisFlag flag = (CMSecurity.DisFlag)CMath.s_valueOf(CMSecurity.DisFlag.values(), lastDisable);
			if(flag==null)
				return " @break@";
			return flag.description();
		}


		final String lastDebug=httpReq.getUrlParameter("DEBUGFLAG");
		if(parms.containsKey("ISDEBUGGING"))
		{
			return Log.debugChannelOn()?"true":"false";
		}
		if(parms.containsKey("DEBUGRESET"))
		{
			if(lastDebug!=null) httpReq.removeUrlParameter("DEBUGFLAG");
			return "";
		}
		if(parms.containsKey("DEBUGNEXT"))
		{
			String lastID="";
			for(final CMSecurity.DbgFlag flag : CMSecurity.DbgFlag.values())
			{
				if((lastDebug==null)||((lastDebug.length()>0)&&(lastDebug.equals(lastID))&&(!flag.toString().equals(lastID))))
				{
					httpReq.addFakeUrlParameter("DEBUGFLAG",flag.toString());
					return "";
				}
				lastID=flag.toString();
			}
			httpReq.addFakeUrlParameter("DEBUGFLAG","");
			if(parms.containsKey("EMPTYOK"))
				return "<!--EMPTY-->";
			return " @break@";

		}
		if(parms.containsKey("DEBUGID"))
		{
			if(lastDebug==null)
				return " @break@";
			return lastDebug;
		}
		if(parms.containsKey("DEBUGDESC"))
		{
			if(lastDebug==null)
				return " @break@";
			final CMSecurity.DbgFlag flag = (CMSecurity.DbgFlag)CMath.s_valueOf(CMSecurity.DbgFlag.values(), lastDebug);
			if(flag==null)
				return " @break@";
			return flag.description();
		}

		final String query=parms.get("QUERY");
		if((query==null)||(query.length()==0))
			return "";
		if(query.equalsIgnoreCase("DISABLE"))
		{
			final String field=parms.get("FIELD");
			if((field==null)||(field.length()==0))
				return "";
			final CMSecurity.DisFlag flag = (CMSecurity.DisFlag)CMath.s_valueOf(CMSecurity.DisFlag.values(), field.toUpperCase().trim());
			if((flag!=null)&&(CMSecurity.isDisabled(flag)))
				return " CHECKED ";
			return "";
		}
		else
		if(query.equalsIgnoreCase("DEBUG"))
		{
			final String field=parms.get("FIELD");
			if((field==null)||(field.length()==0))
				return "";
			final CMSecurity.DbgFlag flag = (CMSecurity.DbgFlag)CMath.s_valueOf(CMSecurity.DbgFlag.values(), field.toUpperCase().trim());
			if((flag!=null)&&(CMSecurity.isDebugging(flag)))
				return " CHECKED ";
			return "";
		}
		else
		if(query.equalsIgnoreCase("CHANGEDISABLE"))
		{
			final String field=parms.get("FIELD");
			if((field==null)||(field.length()==0))
				return "";
			final String value=parms.get("VALUE");
			CMSecurity.setDisableVar(field,((value!=null)&&(value.equalsIgnoreCase("on"))));
			return "";
		}
		else
		if(query.equalsIgnoreCase("CHANGEDEBUG"))
		{
			final String field=parms.get("FIELD");
			if((field==null)||(field.length()==0))
				return "";
			final String value=parms.get("VALUE");
			final DbgFlag flag = CMSecurity.setDebugVar(field,((value!=null)&&(value.equalsIgnoreCase("on"))));
			if((Thread.currentThread() instanceof CWThread)
			&&((flag==DbgFlag.HTTPACCESS)||(flag==DbgFlag.HTTPREQ)))
			{
				final CWConfig config=((CWThread)Thread.currentThread()).getConfig();
				if(CMSecurity.isDebugging(DbgFlag.HTTPREQ))
					config.setDebugFlag(CMProps.instance().getStr("DBGMSGS"));
				if(CMSecurity.isDebugging(DbgFlag.HTTPACCESS))
					config.setAccessLogFlag(CMProps.instance().getStr("ACCMSGS"));
			}
			return "";
		}
		else
		if(query.equalsIgnoreCase("QUERY"))
		{
			final String field=parms.get("FIELD");
			if((field==null)||(field.length()==0))
				return "";
			if(field.equalsIgnoreCase("DATABASE"))
				return "Database Status: "+CMLib.database().errorStatus();
			return "";
		}
		else
		if(query.equalsIgnoreCase("RESET"))
		{
			final String field=parms.get("FIELD");
			if((field==null)||(field.length()==0))
				return "";
			if(field.equalsIgnoreCase("DATABASE"))
			{
				CMLib.database().resetConnections();
				return "Database successfully reset";
			}
			else
			if(field.equalsIgnoreCase("SAVETHREAD"))
			{

			}
			return "";
		}
		return "";
	}
}
