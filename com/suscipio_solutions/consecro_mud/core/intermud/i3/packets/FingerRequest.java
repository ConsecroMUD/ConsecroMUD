package com.suscipio_solutions.consecro_mud.core.intermud.i3.packets;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.intermud.i3.server.I3Server;


@SuppressWarnings("rawtypes")
public class FingerRequest extends Packet
{
	public FingerRequest()
	{
		super();
		type = Packet.FINGER_REQUEST;
	}
	public FingerRequest(Vector v) throws InvalidPacketException {
		super(v);
		try
		{
			type = Packet.FINGER_REQUEST;
			target_mud=(String)v.elementAt(4);
			target_name=(String)v.elementAt(6);
		}
		catch( final ClassCastException e )
		{
			throw new InvalidPacketException();
		}
	}

	@Override
	public void send() throws InvalidPacketException {
		if( sender_name == null || target_mud == null || sender_mud == null  || target_name == null)
		{
			throw new InvalidPacketException();
		}
		super.send();
	}

	@Override
	public String toString()
	{
		final String cmd="({\"finger-req\",5,\"" + I3Server.getMudName() +
			   "\",\"" + sender_name + "\",\"" + target_mud + "\",0,\"" + target_name + "\",})";
		return cmd;
	}
}
