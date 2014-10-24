package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ChannelsLibrary;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class ChannelInfo extends StdWebMacro
{
	@Override public String name() { return "ChannelInfo"; }

	@Override public boolean isAdminMacro() { return true; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("CHANNEL");
		final StringBuffer str=new StringBuffer("");
		if(parms.containsKey("ALLFLAGS"))
		{
			for(final ChannelsLibrary.ChannelFlag flag : ChannelsLibrary.ChannelFlag.values())
				str.append("FLAG_"+flag.name()).append(", ");
		}
		else
		if(last==null)
			return " @break@";
		if(last.length()>0)
		{
			final int code=CMLib.channels().getChannelIndex(last);
			if(code>=0)
			{
				final ChannelsLibrary.CMChannel C=CMLib.channels().getChannel(code);
				if(parms.containsKey("HELP"))
				{
					StringBuilder s=CMLib.help().getHelpText("CHANNEL_"+last,null,false);
					if(s==null) s=CMLib.help().getHelpText(last,null,false);
					int limit=78;
					if(parms.containsKey("LIMIT")) limit=CMath.s_int(parms.get("LIMIT"));
					str.append(helpHelp(s,limit)).append(", ");
				}
				if(parms.containsKey("ID"))
					str.append(code).append(", ");
				if(parms.containsKey("NAME"))
					str.append(C.name).append(", ");
				if(parms.containsKey("COLOROVERRIDE"))
					str.append(C.colorOverrideStr).append(", ");
				if(parms.containsKey("I3NAME"))
					str.append(C.i3name).append(", ");
				if(parms.containsKey("IMC2NAME"))
					str.append(C.imc2Name).append(", ");
				if(parms.containsKey("MASK"))
					str.append(C.mask).append(", ");
				if(parms.containsKey("FLAGSET"))
					for(final ChannelsLibrary.ChannelFlag flag : ChannelsLibrary.ChannelFlag.values())
						httpReq.addFakeUrlParameter("FLAG_"+flag.name(), C.flags.contains(flag)?(parms.containsKey("SELECTED")?"selected":parms.containsKey("CHECKED")?"checked":"on"):"");
				for(final ChannelsLibrary.ChannelFlag flag : ChannelsLibrary.ChannelFlag.values())
					if(parms.containsKey("FLAG_"+flag.name().toUpperCase().trim()))
						str.append(C.flags.contains(flag)?(parms.containsKey("SELECTED")?"selected":parms.containsKey("CHECKED")?"checked":"on"):"").append(", ");
			}
		}
		String strstr=str.toString();
		if(strstr.endsWith(", "))
			strstr=strstr.substring(0,strstr.length()-2);
		return clearWebMacros(strstr);
	}
}
