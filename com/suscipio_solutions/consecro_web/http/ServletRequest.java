package com.suscipio_solutions.consecro_web.http;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletManager;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletRequest;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletSession;
import com.suscipio_solutions.consecro_web.util.CWConfig;
import com.suscipio_solutions.consecro_web.util.CWThread;



/**
 * This class is instantiated as an means for servlets to get input from the request.
 * It is generally a wrapper for the internal class HTTPRequest, but with more
 * guards against some idiosyncracies of that class.
 * 
 * See the interface for more comment
 
 *
 */
public class ServletRequest implements SimpleServletRequest
{
	private final HTTPRequest 			request;
	private final CWConfig			config;
	private final SimpleServletSession  session;
	
	/**
	 * Construct a servlet request input object
	 * @param session the session assigned to the request
	 * @param request the request being processed
	 */
	public ServletRequest(SimpleServletSession session, HTTPRequest request)
	{
		this.request=request;
		if(Thread.currentThread() instanceof CWThread)
			this.config=((CWThread)Thread.currentThread()).getConfig();
		else
			this.config=null;
		this.session=session;
	}
	
	@Override
	public String getHost()
	{
		return request.getHost();
	}

	@Override
	public String getFullRequest()
	{
		return request.getFullRequest();
	}

	@Override
	public String getUrlPath()
	{
		return request.getUrlPath();
	}

	@Override
	public String getUrlParameter(String name)
	{
		return request.getUrlParameter(name);
	}

	@Override
	public Map<String,String> getUrlParametersCopy()
	{
		return request.getUrlParametersCopy();
	}
	
	@Override
	public boolean isUrlParameter(String name)
	{
		return request.isUrlParameter(name);
	}

	@Override
	public void addFakeUrlParameter(String name, String value)
	{
		request.addFakeUrlParameter(name, value);
	}
	
	@Override
	public String getHeader(String name)
	{
		return request.getHeader(name);
	}

	@Override
	public InetAddress getClientAddress()
	{
		return request.getClientAddress();
	}

	@Override
	public InputStream getBody()
	{
		return request.getBody();
	}

	@Override
	public SimpleServletManager getServletManager()
	{
		return (config!=null)?config.getServletMan():null;
	}
	
	@Override
	public String getCookie(String name)
	{
		return request.getCookie(name);
	}

	@Override
	public List<MultiPartData> getMultiParts()
	{
		return request.getMultiParts();
	}

	@Override
	public Set<String> getUrlParameters()
	{
		return request.getUrlParameters();
	}
	
	@Override 
	public void removeUrlParameter(String name)
	{
		request.removeUrlParameter(name);
	}
	
	@Override
	public Set<String> getCookieNames()
	{
		return request.getCookieNames();
	}
	
	/**
	 * Returns the session object associated with this servlet request
	 * @return the session object
	 */
	@Override
	public SimpleServletSession getSession()
	{
		return session;
	}

	@Override
	public double getSpecialEncodingAcceptability(String type)
	{
		return request.getSpecialEncodingAcceptability(type);
	}

	
	@Override
	public int getClientPort()
	{
		return request.getClientPort();
	}
	
	@Override
	public HTTPMethod getMethod()
	{
		return request.getMethod();
	}
	
	@Override
	public String getFullHost()
	{
		return request.getFullHost();
	}
	
	@Override
	public List<long[]> getRangeAZ()
	{
		return request.getRangeAZ();
	}
	@Override
	public Logger getLogger()
	{
		return config.getLogger();
	}

	@Override
	public Map<String,Object> getRequestObjects()
	{
		return request.getRequestObjects();
	}

	@Override
	public float getHttpVer()
	{
		return request.getHttpVer();
	}
}
