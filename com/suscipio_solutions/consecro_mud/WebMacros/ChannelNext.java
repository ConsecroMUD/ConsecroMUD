package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;
import com.suscipio_solutions.consecro_web.util.CWThread;


public class ChannelNext extends StdWebMacro
{
	@Override public String name() { return "ChannelNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		String last=httpReq.getUrlParameter("CHANNEL");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("CHANNEL");
			return "";
		}
		final MOB mob = Authenticate.getAuthenticatedMob(httpReq);
		boolean allChannels=false;
		if((Thread.currentThread() instanceof CWThread)
		&&CMath.s_bool(((CWThread)Thread.currentThread()).getConfig().getMiscProp("ADMIN"))
		&&parms.containsKey("ALLCHANNELS"))
			allChannels=true;
		String lastID="";
		for(int i=0;i<CMLib.channels().getNumChannels();i++)
		{
			final String name=CMLib.channels().getChannel(i).name;
			if((last==null)
			||((last.length()>0)&&(last.equals(lastID))&&(!name.equals(lastID))))
			{
				if(allChannels||((mob!=null)&&(CMLib.channels().mayReadThisChannel(mob,i,true))))
				{
					httpReq.addFakeUrlParameter("CHANNEL",name);
					return "";
				}
				last=name;
			}
			lastID=name;
		}
		httpReq.addFakeUrlParameter("CHANNEL","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
