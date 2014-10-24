package com.suscipio_solutions.consecro_mud.Items.Software;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Items.interfaces.Electronics;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ImmortalOnly;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;


public class ShipTelnetProgram extends GenShipProgram implements ImmortalOnly
{
	@Override public String ID(){	return "StdShipTelnetProgram";}

	protected Socket sock = null;
	protected BufferedInputStream reader=null;
	protected BufferedWriter writer=null;
	protected volatile long nextPowerCycleTmr = System.currentTimeMillis()+(8*1000);

	public ShipTelnetProgram()
	{
		super();
		setName("a telnet disk");
		setDisplayText("a small disk sits here.");
		setDescription("It appears to be a telnet program.");

		material=RawMaterial.RESOURCE_STEEL;
		baseGoldValue=1000;
		recoverPhyStats();
	}

	@Override public String getParentMenu() { return ""; }

	@Override public String getInternalName() { return "TELNET";}

	@Override
	public boolean isActivationString(String word)
	{
		return isCommandString(word,false);
	}

	@Override
	public boolean isDeActivationString(String word)
	{
		return isCommandString(word,false);
	}

	@Override
	public void onDeactivate(MOB mob, String message)
	{
		shutdown();
		super.addScreenMessage("Telnet connection closed.");
	}

	@Override
	public boolean isCommandString(String word, boolean isActive)
	{
		if(!isActive)
		{
			word=word.toUpperCase();
			return (word.startsWith("TELNET ")||word.equals("TELNET"));
		}
		else
		{
			return true;
		}
	}

	@Override
	public String getActivationMenu()
	{
		return "TELNET [HOST] [PORT]: Telnet Network Software";
	}

	protected void shutdown()
	{
		currentScreen="";
		synchronized(this)
		{
			try
			{
				try
				{
					if(sock!=null)
					{
						sock.shutdownInput();
						sock.shutdownOutput();
					}
					if(reader!=null)
						reader.close();
					if(writer!=null)
						writer.close();
				}
				catch(final Exception e) {}
				finally
				{
					sock.close();
				}
			}
			catch(final Exception e) {}
			finally
			{
				sock=null;
				reader=null;
				writer=null;
			}
		}
	}

	@Override
	public boolean checkActivate(MOB mob, String message)
	{
		final List<String> parsed=CMParms.parse(message);
		if(parsed.size()!=3)
		{
			mob.tell(L("Incorrect usage, try: TELNET [HOST] [PORT]"));
			return false;
		}
		try
		{
			shutdown();
			synchronized(this)
			{
				sock=new Socket(parsed.get(1),CMath.s_int(parsed.get(2)));
				sock.setSoTimeout(1);
				reader=new BufferedInputStream(sock.getInputStream());
				writer=new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			}
			currentScreen="Connected to "+parsed.get(1)+" at port "+parsed.get(2);
			fillWithData();
			return true;
		}
		catch(final Exception e)
		{
			mob.tell(L("Telnet software failure: @x1",e.getMessage()));
			return false;
		}
	}

	@Override
	public boolean checkDeactivate(MOB mob, String message)
	{
		shutdown();
		return true;
	}

	@Override
	public boolean checkTyping(MOB mob, String message)
	{
		synchronized(this)
		{
			if(sock!=null)
				return true;
		}
		mob.tell(L("Software failure."));
		super.forceUpMenu();
		super.forceNewMenuRead();
		return true;
	}

	@Override
	public boolean checkPowerCurrent(int value)
	{
		nextPowerCycleTmr=System.currentTimeMillis()+(8*1000);
		return true;
	}

	public void fillWithData()
	{
		try
		{
			synchronized(this)
			{
				if(reader!=null)
				{
					final ByteArrayOutputStream bout=new ByteArrayOutputStream();
					while(reader.available()>0)
					{
						final int c=reader.read();
						if(c==-1)
							throw new IOException("Received EOF");
						if(c!=0)
							bout.write(c);
					}
					if(bout.size()>0)
						super.addScreenMessage(new String(bout.toByteArray(),"UTF-8"));

				}
			}
		}
		catch(final java.net.SocketTimeoutException se)
		{

		}
		catch(final Exception e)
		{
			super.addScreenMessage("*** Telnet disconnected: "+e.getMessage()+" ***");
			super.forceNewMessageScan();
			shutdown();
			super.forceUpMenu();
			super.forceNewMenuRead();
		}
	}

	@Override
	public void onTyping(MOB mob, String message)
	{
		synchronized(this)
		{
			if(writer!=null)
			{
				try
				{
					writer.write(message+"\n\r");
					writer.flush();
				}
				catch (final IOException e)
				{
					super.addScreenMessage("*** Telnet disconnected: "+e.getMessage()+" ***");
					super.forceNewMessageScan();
					shutdown();
					super.forceUpMenu();
					super.forceNewMenuRead();
				}
			}
		}
	}

	@Override
	public void onPowerCurrent(int value)
	{
		if(value>0)
			fillWithData();
		if((container() instanceof Electronics.Computer)
		&&(((Electronics.Computer)container()).getCurrentReaders().size()==0))
		{
			this.shutdown();
		}
		else
		if(System.currentTimeMillis()>nextPowerCycleTmr)
		{
			this.shutdown();
		}
	}
}
