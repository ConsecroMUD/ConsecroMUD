package com.suscipio_solutions.consecro_mud.core.intermud.i3.packets;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.intermud.i3.server.I3Server;


@SuppressWarnings("rawtypes")
public class PingPacket extends Packet
{
	public PingPacket()
	{
		super();
		type = Packet.PING_PACKET;
		target_mud=Intermud.getNameServer().name;
	}

	public PingPacket(Vector v)
	{
		super(v);
		type = Packet.PING_PACKET;
		target_mud=v.elementAt(4).toString();
	}

	public PingPacket(String mud)
	{
		super();
		type = Packet.PING_PACKET;
		target_mud=mud;
	}

	@Override
	public void send() throws InvalidPacketException
	{
		super.send();
	}

	@Override
	public String toString()
	{
		return "({\"ping-req\",5,\""+I3Server.getMudName()+"\",0,\""+target_mud+"\",0,0,})";
	}
}
