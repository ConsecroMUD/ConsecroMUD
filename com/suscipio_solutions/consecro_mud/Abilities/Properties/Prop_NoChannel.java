package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_NoChannel extends Property
{
	@Override public String ID() { return "Prop_NoChannel"; }
	@Override public String name(){ return "Channel Neutralizing";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS;}
	protected List<String> channels=null;
	protected boolean receive=true;
	protected boolean sendOK=false;

	@Override
	public String accountForYourself()
	{ return "No Channeling Field"; }

	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		channels=CMParms.parseSemicolons(newText.toUpperCase(),true);
		int x=channels.indexOf("SENDOK");
		sendOK=(x>=0);
		if(sendOK)
			channels.remove(x);
		x=channels.indexOf("QUIET");
		receive=(x<0);
		if(!receive)
			channels.remove(x);
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;


		if(msg.othersMajor(CMMsg.MASK_CHANNEL))
		{
			final int channelInt=msg.othersMinor()-CMMsg.TYP_CHANNEL;
			if((msg.source()==affected)||(!(affected instanceof MOB))
			&&((channels==null)||(channels.size()==0)||(channels.contains(CMLib.channels().getChannel(channelInt).name))))
			{
				if(!sendOK)
				{
					if(msg.source()==affected)
						msg.source().tell(L("Your message drifts into oblivion."));
					else
					if((!(affected instanceof MOB))
					&&(CMLib.map().roomLocation(affected)==msg.source().location()))
						msg.source().tell(L("This is a no-channel area."));
					return false;
				}
				if(!receive)
				{
					if((msg.source()!=affected)
					||((!(affected instanceof MOB))&&(CMLib.map().roomLocation(affected)!=msg.source().location())))
						return false;
				}
			}
		}
		return true;
	}
}
