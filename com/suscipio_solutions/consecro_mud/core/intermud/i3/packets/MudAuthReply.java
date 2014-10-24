package com.suscipio_solutions.consecro_mud.core.intermud.i3.packets;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.intermud.i3.server.I3Server;


@SuppressWarnings("rawtypes")
public class MudAuthReply extends Packet
{
	public long key=0;

	public MudAuthReply()
	{
		super();
		type = Packet.MAUTH_REPLY;
		target_mud=I3Server.getMudName();
	}

	public MudAuthReply(Vector v)
	{
		super(v);
		type = Packet.MAUTH_REPLY;
		target_mud=(String)v.elementAt(4);
		key=CMath.s_int(v.elementAt(6).toString());
	}

	public MudAuthReply(String mud, long key)
	{
		super();
		type = Packet.MAUTH_REPLY;
		target_mud=mud;
		this.key=key;
	}

	@Override
	public void send() throws InvalidPacketException
	{
		super.send();
	}

	@Override
	public String toString()
	{
		return "({\"auth-mud-reply\",5,\""+I3Server.getMudName()+"\",0,\""+target_mud+"\",0,"+key+",})";
	}
}
