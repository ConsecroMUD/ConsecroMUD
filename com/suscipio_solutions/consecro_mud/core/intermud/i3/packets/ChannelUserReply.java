package com.suscipio_solutions.consecro_mud.core.intermud.i3.packets;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.intermud.i3.server.I3Server;


@SuppressWarnings("rawtypes")
public class ChannelUserReply extends Packet {
	public String userRequested=null;
	public String userVisibleName=null;
	public char gender = 'N';

	public ChannelUserReply()
	{
		super();
		type = Packet.CHAN_USER_REP;
	}

	public ChannelUserReply(Vector v) throws InvalidPacketException {
		super(v);
		try
		{
			type = Packet.CHAN_USER_REP;
			try
			{
				userRequested = (String)v.elementAt(6);
				userVisibleName = (String)v.elementAt(7);
				final int gend = CMath.s_int(v.elementAt(8).toString());
				switch(gend)
				{
				case 0: gender='M'; break;
				case 1: gender='F'; break;
				case 2: gender='N'; break;
				}
			}catch(final Exception e){}
		}
		catch( final ClassCastException e )
		{
			throw new InvalidPacketException();
		}
	}

	@Override
	public void send() throws InvalidPacketException {
		if( userRequested == null || userVisibleName == null )
		{
			throw new InvalidPacketException();
		}
		super.send();
	}

	@Override
	public String toString()
	{
		int genderCode = 0;
		switch(gender)
		{
		case 'M': genderCode=0; break;
		case 'F': genderCode=1; break;
		case 'N': genderCode=2; break;
		}
		final String cmd="({\"chan-user-req\",5,\"" + I3Server.getMudName() +
		"\",0,\"" + target_mud + "\",0,\"" + userRequested
		+ "\",\"" + userVisibleName
		+ "\"," + genderCode + ",})";
		return cmd;

	}
}
