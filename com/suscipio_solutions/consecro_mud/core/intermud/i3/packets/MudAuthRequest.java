package com.suscipio_solutions.consecro_mud.core.intermud.i3.packets;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.intermud.i3.server.I3Server;


@SuppressWarnings("rawtypes")
public class MudAuthRequest extends Packet
{
	public MudAuthRequest()
	{
		super();
		type = Packet.MAUTH_REQUEST;
		target_mud=I3Server.getMudName();
	}

	public MudAuthRequest(Vector v)
	{
		super(v);
		type = Packet.MAUTH_REQUEST;
		target_mud=(String)v.elementAt(4);
	}

	public MudAuthRequest(String target_mud)
	{
		super();
		type = Packet.MAUTH_REQUEST;
		this.target_mud=target_mud;
	}

	@Override
	public void send() throws InvalidPacketException
	{
		super.send();
	}

	@Override
	public String toString()
	{
		return "({\"auth-mud-req\",5,\""+I3Server.getMudName()+"\",0,\""+target_mud+"\",0,})";
	}
}
