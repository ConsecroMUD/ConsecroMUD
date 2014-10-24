package com.suscipio_solutions.consecro_web.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;



/**
 * A wrapper for a foreign host, port, and context
 
 *
 */
public class WebAddress
{
	private final String				host;
	private final int					port;
	private final InetSocketAddress		address;
	private final String				context;
	
	public WebAddress(String host, int port, String context) throws UnknownHostException
	{
		this.host=host;
		this.port=port;
		this.address=new InetSocketAddress(InetAddress.getByName(host), port);
		this.context=context;
	}

	/**
	 * @return the host
	 */
	public InetSocketAddress getAddress()
	{
		return address;
	}

	/**
	 * @return the context
	 */
	public String getContext()
	{
		return context;
	}
	
	/**
	 * 
	 * @return the host
	 */
	public String getHost()
	{
		return host;
	}
	
	/**
	 * 
	 * @return the port
	 */
	public int getPort()
	{
		return port;
	}
	
	/**
	 * 
	 * @return the host + port
	 */
	public String getAddressStr() 
	{ 
		return getHost()+":"+getPort();
	}
}
