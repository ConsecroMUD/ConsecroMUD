package com.suscipio_solutions.siplet.support;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import com.suscipio_solutions.siplet.applet.Siplet;
import com.suscipio_solutions.siplet.support.MiniJSON.MJSONException;


public class GMCP
{
	public GMCP(){super();}

	public int process(StringBuffer buf, int i, Siplet applet, boolean useExternal)
	{
		return 0;
	}

	public String gmcpReceive(byte[] buffer)
	{
		return new String(buffer,Charset.forName("US-ASCII"));
	}

	public byte[] convertStringToGmcp(String data) throws MJSONException
	{
		try
		{
			data=data.trim();
			String cmd;
			String parms;
			final int x=data.indexOf(' ');
			if(x<0)
			{
				cmd=data;
				parms="";
			}
			else
			{
				cmd=data.substring(0, x).trim();
				parms=data.substring(x+1).trim();
			}
			if(cmd.length()==0)
				return new byte[0];
			if(parms.length()>0)
			{
				final MiniJSON jsonParser=new MiniJSON();
				jsonParser.parseObject("{\"root\":"+parms+"}");
				// simple parse test.. should throw an exception
			}
			else
				parms="{}";
			final ByteArrayOutputStream bout=new ByteArrayOutputStream();
			bout.write(TelnetFilter.IAC_);
			bout.write(TelnetFilter.IAC_SB);
			bout.write(TelnetFilter.IAC_GMCP);
			bout.write((cmd+" "+parms).getBytes("US-ASCII"));
			bout.write(TelnetFilter.IAC_);
			bout.write(TelnetFilter.IAC_SE);
			return bout.toByteArray();
		}
		catch (final Exception e)
		{
			return new byte[0];
		}
	}
}
