package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ChannelsLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class ChannelBackLogNext extends StdWebMacro
{
	@Override public String name() { return "ChannelBackLogNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		String last=httpReq.getUrlParameter("CHANNELBACKLOG");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("CHANNELBACKLOG");
			return "";
		}
		final String channel=httpReq.getUrlParameter("CHANNEL");
		if(channel==null) return " @break@";
		final int channelInt=CMLib.channels().getChannelIndex(channel);
		if(channelInt<0) return " @break@";
		final MOB mob = Authenticate.getAuthenticatedMob(httpReq);
		if(mob!=null)
		{
			if(CMLib.channels().mayReadThisChannel(mob,channelInt,true))
			{
				@SuppressWarnings("unchecked")
				List<ChannelsLibrary.ChannelMsg> que=(List<ChannelsLibrary.ChannelMsg>)httpReq.getRequestObjects().get("CHANNELMSG_"+channelInt+" QUE");
				if(que==null)
				{
					final List<ChannelsLibrary.ChannelMsg> oldQue=CMLib.channels().getChannelQue(channelInt);
					que=new Vector<ChannelsLibrary.ChannelMsg>(oldQue.size());
					que.addAll(oldQue);
					httpReq.getRequestObjects().put("CHANNELMSG_"+channelInt+" QUE",que);
				}

				while(true)
				{
					final int num=CMath.s_int(last);
					last=""+(num+1);
					httpReq.addFakeUrlParameter("CHANNELBACKLOG",last);
					if((num<0)||(num>=que.size()))
					{
						httpReq.addFakeUrlParameter("CHANNELBACKLOG","");
						if(parms.containsKey("EMPTYOK"))
							return "<!--EMPTY-->";
						return " @break@";
					}
					final boolean areareq=CMLib.channels().getChannel(channelInt).flags.contains(ChannelsLibrary.ChannelFlag.SAMEAREA);

					final ChannelsLibrary.ChannelMsg cmsg=que.get(num);
					final CMMsg msg=cmsg.msg;
					String str=null;
					if((mob==msg.source())&&(msg.sourceMessage()!=null))
						str=msg.sourceMessage();
					else
					if((mob==msg.target())&&(msg.targetMessage()!=null))
						str=msg.targetMessage();
					else
					if(msg.othersMessage()!=null)
						str=msg.othersMessage();
					else
						str="";
					str=CMStrings.removeColors(str);
					str += " ("+CMLib.time().date2SmartEllapsedTime(Math.round((System.currentTimeMillis()-cmsg.ts)/1000)*1000,false)+" ago)";
					if(CMLib.channels().mayReadThisChannel(msg.source(),areareq,mob,channelInt,true))
						return clearWebMacros(CMLib.coffeeFilter().fullOutFilter(mob.session(),mob,msg.source(),msg.target(),msg.tool(),CMStrings.removeColors(str),false));
				}
			}
			return "";
		}
		return "";
	}
}
