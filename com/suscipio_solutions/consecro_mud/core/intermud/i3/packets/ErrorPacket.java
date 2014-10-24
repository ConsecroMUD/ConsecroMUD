package com.suscipio_solutions.consecro_mud.core.intermud.i3.packets;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.intermud.i3.server.I3Server;


@SuppressWarnings("rawtypes")
public class ErrorPacket extends Packet
{
	public String error_code="";
	public String error_message = "";
	public String packetStr = "";

	public ErrorPacket()
	{
		super();
		type = Packet.ERROR_PACKET;
	}

	public ErrorPacket(String to_whom, String mud, String error_code, String error_message, String packetStr)
	{
		super();
		type = Packet.ERROR_PACKET;
		target_mud = mud;
		target_name = to_whom;
		this.error_code=error_code;
		this.error_message=error_message;
		this.packetStr=packetStr;
	}

	public ErrorPacket(Vector v) throws InvalidPacketException
	{
		super(v);
		try
		{
			type = Packet.ERROR_PACKET;
			try
			{
				error_code = v.elementAt(6).toString();
				error_message = v.elementAt(7).toString();
				packetStr=v.elementAt(8).toString();
			}catch(final Exception e){ }
		}
		catch( final ClassCastException e )
		{
			throw new InvalidPacketException();
		}
	}

	@Override
	public void send() throws InvalidPacketException
	{
		super.send();
	}

	@Override
	public String toString()
	{
		final String cmd = "({\"error\",5,\"" + I3Server.getMudName() +
				 "\",0,\"" + target_mud + "\",\"" + target_name + "\"," +
				 "\""+error_code+"\",\""+error_message+"\","+packetStr+",})";
		return cmd;

	}
}
