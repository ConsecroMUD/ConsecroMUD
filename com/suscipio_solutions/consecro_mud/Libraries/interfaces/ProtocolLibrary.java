package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.Map;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;

public interface ProtocolLibrary extends CMLibrary
{
	public String msp(final String soundName, final int priority);

	public String[] mxpImagePath(String fileName);
	public String mxpImage(final Environmental E, final String parms);
	public String mxpImage(final Environmental E, final String parms, final String pre, final String post);
	public String getDefaultMXPImage(final Object O);

	public byte[] processMsdp(final Session session, final char[] data, final int dataSize, final Map<Object,Object> reportables);
	public byte[] pingMsdp(final Session session, final Map<Object,Object> reportables);

	public byte[] processGmcp(final Session session, final String data, final Map<String,Double> supportables);
	public byte[] buildGmcpResponse(String json);
	public byte[] pingGmcp(final Session session, final Map<String,Long> reporteds, final Map<String,Double> supportables);
	
	public enum gmcpCommand
	{
		core_hello,
		core_supports_set,
		core_supports_add,
		core_supports_remove,
		core_keepalive,
		core_ping,
		core_goodbye,
		char_vitals,
		char_statusvars,
		char_status,
		char_base,
		char_maxstats,
		char_worth,
		char_items_inv, // means they want updates, dude
		char_items_contents,
		char_skills_get,
		group,
		room_info, // means they want room.wrongdir
		comm_channel,
		comm_channel_players,
		ire_composer_setbuffer
	}
}
