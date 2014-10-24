package com.suscipio_solutions.consecro_mud.core.interfaces;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;


/**
 * An object which is permitted to monitor game events in CoffeeMud.
 */
public interface MsgMonitor
{
	/**
	 * The general message event monitor for the object.  The messages
	 * have already been through an approval process, so this method is
	 * called only to see the final execution of the meaning of the
	 * message.
	 * @param room the room the message was sent to
	 * @param msg the CMMsg that needs to be executed
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg
	 */
	public void monitorMsg(Room room, CMMsg msg);
}
