package com.suscipio_solutions.consecro_web.util;



/**
 * A basic thread for all request management.  It's main purpose
 * is simply to carry the configuration for the server around
 
 *
 */
public class CWThread extends Thread
{
	private final CWConfig config;
	
	public CWThread(CWConfig config, Runnable r, String name)
	{
		super(r, name);
		this.config=config;
	}
	
	public CWThread(CWConfig config, String name)
	{
		super(name);
		this.config=config;
	}
	
	public CWConfig getConfig()
	{
		return config;
	}
	
	public String toString()
	{
		final StringBuilder dump = new StringBuilder("");
		final java.lang.StackTraceElement[] s=getStackTrace();
		for (final StackTraceElement element : s)
			dump.append(element.getClassName()+": "+element.getMethodName()+"("+element.getFileName()+": "+element.getLineNumber()+") | ");
		return dump.toString();
	}
}
