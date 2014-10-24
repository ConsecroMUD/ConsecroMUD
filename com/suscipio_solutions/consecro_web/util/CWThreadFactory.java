package com.suscipio_solutions.consecro_web.util;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * A custom thread factory whose only virtue is that it gives nice names to the
 * threads to make them easier to track.
 
 *
 */
public class CWThreadFactory implements ThreadFactory  
{
	private String 						serverName;
	private final AtomicInteger 		counter		= new AtomicInteger();
	private final LinkedList<Thread> 	active 		= new LinkedList<Thread>();
	private final CWConfig			config;
	
	public CWThreadFactory(String serverName, CWConfig config)
	{
		this.serverName=serverName;
		this.config=config;
	}
	public void setServerName(String newName)
	{
		this.serverName=newName;
	}
	@Override
	public Thread newThread(Runnable r) 
	{
		final Thread t = new CWThread(config, r,"cweb-"+serverName+"#"+counter.addAndGet(1));
		active.add(t);
		return t;
	}
	public Collection<Thread> getThreads() 
	{ 
		return active;
	}
}
