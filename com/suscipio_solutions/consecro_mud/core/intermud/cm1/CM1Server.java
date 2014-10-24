package com.suscipio_solutions.consecro_mud.core.intermud.cm1;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.collections.SHashtable;


public class CM1Server extends Thread
{
	private String		name = "";
	private int 		port = 27755;
	private boolean 	shutdownRequested = false;
	private boolean 	isShutdown = false;
	private Selector	servSelector = null;
	private ServerSocketChannel
						servChan = null;
	private final SHashtable<SocketChannel,RequestHandler>
						handlers = new SHashtable<SocketChannel,RequestHandler>();
	private final String		iniFile;
	private CMProps 	page;



	public CM1Server(String serverName, String iniFile)
	{
		super(serverName);
		if(!loadPropPage(iniFile))
			throw new IllegalArgumentException();
		final int serverPort = page.getInt("PORT");
		this.iniFile=iniFile;
		name=serverName+"@"+serverPort;
		setName(name);
		port=serverPort;
		shutdownRequested = false;
	}

	public String getINIFilename() { return iniFile;}

	protected boolean loadPropPage(String iniFile)
	{
		if (page==null || !page.isLoaded())
		{
			page=new CMProps (iniFile);
			if(!page.isLoaded())
			{
				Log.errOut(getName(),"failed to load " + iniFile);
				return false;
			}
		}
		return true;
	}

	@Override
	public void run()
	{
		while(!shutdownRequested)
		{
			try
			{
				servChan = ServerSocketChannel.open();
				final ServerSocket serverSocket = servChan.socket();
				servSelector = Selector.open();
				if((page.getStr("BIND")!=null)&&(page.getStr("BIND").trim().length()>0))
					serverSocket.bind (new InetSocketAddress(InetAddress.getByName(page.getStr("BIND")),port));
				else
					serverSocket.bind (new InetSocketAddress (port));
				Log.sysOut("Started "+name+" on port "+port);
				servChan.configureBlocking (false);
				servChan.register (servSelector, SelectionKey.OP_ACCEPT);
			}
			catch(final IOException e)
			{
				Log.errOut(e);
				Log.errOut("CM1Server failed to start.");
				shutdownRequested=true;
				break;
			}
			try
			{
				shutdownRequested = false;
				while (!shutdownRequested)
				{
					try
					{
						final int n = servSelector.select();
						if (n == 0) continue;

						final Iterator<SelectionKey> it = servSelector.selectedKeys().iterator();
						while (it.hasNext())
						{
							final SelectionKey key = it.next();
							if (key.isAcceptable())
							{
								final ServerSocketChannel server = (ServerSocketChannel) key.channel();
								final SocketChannel channel = server.accept();
								if (channel != null)
								{
									final RequestHandler handler=new RequestHandler(channel,page.getInt("IDLETIMEOUTMINS"));
									channel.configureBlocking (false);
									channel.register (servSelector, SelectionKey.OP_READ, handler);
									handlers.put(channel,handler);
									handler.sendMsg("CONNECTED TO "+name.toUpperCase());
								}
								//sayHello (channel);
							}
							try
							{
								if (key.isReadable())
								{
									final RequestHandler handler = (RequestHandler)key.attachment();
									if((!handler.isRunning())&&(!handler.needsClosing()))
										CMLib.threads().executeRunnable(handler);
								}
							}
							finally
							{
								it.remove();
							}
						}
						for(final SocketChannel schan : handlers.keySet())
							try
							{
								final RequestHandler handler=handlers.get(schan);
								if((handler!=null)&&(handler.needsClosing()))
									handler.shutdown();
							}
							catch(final Exception e){}
					}
					catch(final CancelledKeyException t)
					{
						// ignore
					}
				}
			}
			catch(final Exception t)
			{
				Log.errOut("CM1Server",t);
			}
			finally
			{
				if(servSelector != null)
					try {servSelector.close();}catch(final Exception e){}
				if(servChan != null)
					try {servChan.close();}catch(final Exception e){}
				for(final SocketChannel schan : handlers.keySet())
					try
					{
						final RequestHandler handler=handlers.get(schan);
						if(handler!=null)handler.shutdown();
					}
					catch(final Exception e){}
				handlers.clear();
				Log.sysOut("CM1Server","Shutdown complete");
			}
		}
		isShutdown = true;
	}

	public void shutdown()
	{
		shutdownRequested = true;
		final long time = System.currentTimeMillis();
		while((System.currentTimeMillis()-time<30000) && (!isShutdown))
		{
			try {Thread.sleep(1000);}catch(final Exception e){}
			if(servSelector != null)
				try {servSelector.close();}catch(final Exception e){}
			try {Thread.sleep(1000);}catch(final Exception e){}
			if((servChan != null)&&(!isShutdown))
				try {servChan.close();}catch(final Exception e){}
			try {Thread.sleep(1000);}catch(final Exception e){}
			this.interrupt();
		}
	}
}
