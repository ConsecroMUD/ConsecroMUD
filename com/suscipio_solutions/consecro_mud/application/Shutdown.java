package com.suscipio_solutions.consecro_mud.application;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;

public class Shutdown
{

	public Shutdown()
	{
		super();
	}

	public static void main(String a[])
	{
		if(a.length<4)
		{
			System.out.println("Command usage: Shutdown <host> <port> <username> <password> (<true/false for reboot> <external command>)");
			return;
		}
		Socket sock=null;
		try
		{
			final StringBuffer msg=new StringBuffer("\033[1z<SHUTDOWN "+a[2]+" "+a[3]);
			if(a.length>=5)
				msg.append(" "+!(CMath.s_bool(a[4])));
			if(a.length>=6)
				for(int i=5;i<a.length;i++)
				msg.append(" "+a[i]);
			sock=new Socket(a[0],CMath.s_int(a[1]));
			final OutputStream rawout=sock.getOutputStream();
			rawout.write(CMStrings.strToBytes((msg.toString()+">\n")));
			rawout.flush();
			final BufferedReader in=new BufferedReader(new InputStreamReader(sock.getInputStream()));
			String read="";
			while(!read.startsWith("\033[1z<"))
				read=in.readLine();
			System.out.println(read.substring("\033[1z<".length()));
		}
		catch(final Exception e){e.printStackTrace();}
		finally { if(sock!=null) try{ sock.close(); } catch (final IOException e) { } }
	}
}
