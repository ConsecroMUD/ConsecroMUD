package com.suscipio_solutions.consecro_mud.core.intermud.i3.packets;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.intermud.i3.server.I3Server;


@SuppressWarnings("rawtypes")
public class ChannelUserRequest extends Packet {
	public String userToRequest = null;

	public ChannelUserRequest()
	{
		super();
		type = Packet.CHAN_USER_REQ;
	}
	public ChannelUserRequest(Vector v) throws InvalidPacketException {
		super(v);
		try
		{
			type = Packet.CHAN_USER_REQ;
			userToRequest = (String)v.elementAt(6);
		}
		catch( final ClassCastException e )
		{
			throw new InvalidPacketException();
		}
	}

	@Override
	public void send() throws InvalidPacketException {
		if( sender_name == null || target_mud == null || sender_mud == null  || userToRequest == null)
		{
			throw new InvalidPacketException();
		}
		super.send();
	}

	@Override
	public String toString()
	{
		final String cmd="({\"chan-user-req\",5,\"" + I3Server.getMudName() +
			   "\",0,\"" + target_mud + "\",0,\"" + userToRequest + "\",})";
		return cmd;
	}
}
