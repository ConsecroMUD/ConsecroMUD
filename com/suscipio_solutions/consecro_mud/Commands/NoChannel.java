package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerStats;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ChannelsLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings("rawtypes")
public class NoChannel extends StdCommand
{
	public NoChannel(){}

	private final String[] access=null;
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final PlayerStats pstats=mob.playerStats();
		if(pstats==null) return false;
		String channelName=((String)commands.elementAt(0)).toUpperCase().trim().substring(2);
		commands.removeElementAt(0);
		int channelNum=-1;
		for(int c=0;c<CMLib.channels().getNumChannels();c++)
		{
			final ChannelsLibrary.CMChannel chan=CMLib.channels().getChannel(c);
			if(chan.name.equalsIgnoreCase(channelName))
			{
				channelNum=c;
				channelName=chan.name;
			}
		}
		if(channelNum<0)
		for(int c=0;c<CMLib.channels().getNumChannels();c++)
		{
			final ChannelsLibrary.CMChannel chan=CMLib.channels().getChannel(c);
			if(chan.name.toUpperCase().startsWith(channelName))
			{
				channelNum=c;
				channelName=chan.name;
			}
		}
		if((channelNum<0)
		||(!CMLib.masking().maskCheck(CMLib.channels().getChannel(channelNum).mask,mob,true)))
		{
			mob.tell(L("This channel is not available to you."));
			return false;
		}
		if(!CMath.isSet(pstats.getChannelMask(),channelNum))
		{
			pstats.setChannelMask(pstats.getChannelMask()|(1<<channelNum));
			mob.tell(L("The @x1 channel has been turned off.  Use `@x2` to turn it back on.",channelName,channelName.toUpperCase()));
		}
		else
			mob.tell(L("The @x1 channel is already off.",channelName));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
