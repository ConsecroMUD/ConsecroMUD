package com.suscipio_solutions.consecro_mud.core.intermud.i3.packets;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.intermud.i3.server.I3Server;


@SuppressWarnings("rawtypes")
public class ShutdownPacket extends Packet
{
	public ShutdownPacket()
	{
		super();
		type = Packet.SHUTDOWN;
	}

	public ShutdownPacket(Vector v)
	{
		super(v);
		type = Packet.SHUTDOWN;
		target_mud=Intermud.getNameServer().name;
	}

	@Override
	public void send() throws InvalidPacketException
	{
		super.send();
	}

	@Override
	public String toString()
	{
		return "({\"shutdown\",5,\""+I3Server.getMudName()+"\",0,\""+target_mud+"\",0,0,})";
	}
}
