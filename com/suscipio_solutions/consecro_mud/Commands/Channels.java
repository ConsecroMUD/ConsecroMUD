package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerStats;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ListingLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings("rawtypes")
public class Channels extends StdCommand
{
	public Channels(){}

	private final String[] access=I(new String[]{"CHANNELS"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final PlayerStats pstats=mob.playerStats();
		if(pstats==null) return false;
		final StringBuffer buf=new StringBuffer(L("Available channels: \n\r"));
		int col=0;
		final String[] names=CMLib.channels().getChannelNames();
		final int COL_LEN=ListingLibrary.ColFixer.fixColWidth(24.0,mob);
		for(int x=0;x<names.length;x++)
			if(CMLib.masking().maskCheck(CMLib.channels().getChannel(x).mask,mob,true))
			{
				if((++col)>3)
				{
					buf.append("\n\r");
					col=1;
				}
				final String channelName=names[x];
				final boolean onoff=CMath.isSet(pstats.getChannelMask(),x);
				buf.append(CMStrings.padRight("^<CHANNELS '"+(onoff?"":"NO")+"'^>"+channelName+"^</CHANNELS^>"+(onoff?" (OFF)":""),COL_LEN));
			}
		if(names.length==0)
			buf.append("None!");
		else
			buf.append("\n\rUse NOCHANNELNAME (ex: NOGOSSIP) to turn a channel off.");
		mob.tell(buf.toString());
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
