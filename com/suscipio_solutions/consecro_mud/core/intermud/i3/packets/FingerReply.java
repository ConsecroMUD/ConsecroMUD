package com.suscipio_solutions.consecro_mud.core.intermud.i3.packets;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.intermud.i3.server.I3Server;


@SuppressWarnings("rawtypes")
public class FingerReply extends Packet {
	public String visible_name="";
	public String title = "";
	public String real_name = "";
	public String e_mail="";
	public String loginout_time = "";
	public String idle_time = "";
	public String ip_time = "";
	public String extra = "";

	public FingerReply()
	{
		super();
		type = Packet.FINGER_REPLY;
	}

	public FingerReply(String to_whom, String mud)
	{
		super();
		type = Packet.FINGER_REPLY;
		target_mud = mud;
		target_name = to_whom;
	}

	public FingerReply(Vector v) throws InvalidPacketException
	{
		super(v);
		try
		{
			type = Packet.FINGER_REPLY;
			try
			{
			visible_name = v.elementAt(6).toString();
			title = v.elementAt(7).toString();
			real_name = v.elementAt(8).toString();
			e_mail = v.elementAt(9).toString();
			loginout_time = v.elementAt(10).toString();
			idle_time = v.elementAt(11).toString();
			ip_time = v.elementAt(12).toString();
			extra = v.elementAt(13).toString();
			}catch(final Exception e){ }
		}
		catch( final ClassCastException e )
		{
			throw new InvalidPacketException();
		}
	}

	@Override
	public void send() throws InvalidPacketException {
		super.send();
	}

	@Override
	public String toString()
	{
		String cmd = "({\"finger-reply\",5,\"" + I3Server.getMudName() +
				 "\",0,\"" + target_mud + "\",\"" + target_name + "\",";
		final String[] responses={ visible_name, title, real_name, e_mail,
							loginout_time, idle_time, ip_time, extra };
		for(final String nom : responses)
		{
			if(nom.length()==0)
				cmd += "0,";
			else
			if(CMath.isNumber(nom))
				cmd += "" + nom + ",";
			else
				cmd += "\"" + nom + "\",";
		}
		cmd += "})";
		return cmd;

	}
}
