package com.suscipio_solutions.consecro_mud.core.intermud.i3.packets;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.intermud.i3.net.Interactive;
import com.suscipio_solutions.consecro_mud.core.intermud.i3.server.I3Server;
/**
 * com.suscipio_solutions.consecro_mud.core.intermud.i3.packets.TellPacket
 * Copyright (c) 1996 George Reese
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The I3 tell packet
 */

/**
 * This extension of the Packet class handles incoming and
 * outgoing intermud tells.  To use it in a services object,
 * simply grab the data out of its public members.  To use
 * it to send a tell, set all of its public data members
 * and call the send() method.<BR>
 * Created: 22 Spetember 1996<BR>
 * Last modified: 29 September 1996
 * @author George Reese (borg@imaginary.com)
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public class TellPacket extends Packet {

	/**
	 * The display name for the person sending the tell.
	 */
	public String sender_visible_name = null;
	/**
	 * The actual message being sent.
	 */
	public String message = null;

	public TellPacket()
	{
		super();
		type = Packet.TELL;
	}

	/**
	 * Constructs a tell package based on an I3 mud mode vector.
	 * @exception com.suscipio_solutions.consecro_mud.core.intermud.i3.packets.InvalidPacketException thrown if the incoming packet is bad
	 * @param v the I3 mud mode vector containing the incoming tell
	 */
	public TellPacket(Vector v) throws InvalidPacketException {
		super(v);
		try
		{
			type = Packet.TELL;
			sender_visible_name = (String)v.elementAt(6);
			message = (String)v.elementAt(7);
		}
		catch( final ClassCastException e )
		{
			throw new InvalidPacketException();
		}
	}

	/**
	 * Constructs an outgoing tell.
	 * @param u the interactive sending the tell
	 * @param who the person whom they are sending the tell to
	 * @param mud the mud the target is on
	 * @param msg the message being sent
	 */
	public TellPacket(Interactive u, String who, String mud, String msg)
	{
		super();
		type = Packet.TELL;
		sender_name = u.getKeyName();
		target_mud = mud;
		target_name = who;
		sender_visible_name = u.getDisplayName();
		message = msg;
	}

	/**
	 * Sends a properly constructed outgoing tell to its target.
	 * @exception com.suscipio_solutions.consecro_mud.core.intermud.i3.packets.InvalidPacketException thrown if this packet was not properly constructed
	 * @see com.suscipio_solutions.consecro_mud.core.intermud.i3.packets.Packet#send
	 */
	@Override
	public void send() throws InvalidPacketException {
		if( message == null || sender_visible_name == null )
		{
			throw new InvalidPacketException();
		}
		message = convertString(message);
		super.send();
	}

	/**
	 * This method is used by the I3 system to turn the packet
	 * into a mud mode string.  To see the proper format for
	 * an I3 tell, see the <A HREF="http://www.imaginary.com/intermud/intermud3.html">
	 * Intermud 3</A> documentation.
	 * @return the mud mode string for this packet
	 */
	@Override
	public String toString()
	{
		return "({\"tell\",5,\"" + I3Server.getMudName() +
			   "\",\"" + sender_name + "\",\"" + target_mud +
			   "\",\"" + target_name + "\",\"" +
			   sender_visible_name + "\",\"" + message + "\",})";
	}
}
