package com.suscipio_solutions.consecro_mud.core.threads;
import java.util.Collection;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.suscipio_solutions.consecro_mud.core.collections.SLinkedList;


public class CMThreadFactory implements ThreadFactory
{
	private String 						serverName;
	private final AtomicInteger 		counter		=new AtomicInteger();
	private final SLinkedList<Thread> 	active 		= new SLinkedList<Thread>();
	private final ThreadGroup			threadGroup;

	public CMThreadFactory(String serverName)
	{
		this.serverName=serverName;
		this.threadGroup=Thread.currentThread().getThreadGroup();
	}
	public void setServerName(String newName)
	{
		this.serverName=newName;
	}
	@Override
	public Thread newThread(Runnable r)
	{
		final Thread t = new CMFactoryThread(threadGroup,r,serverName+"#"+counter.addAndGet(1));
		active.add(t);
		return t;
	}
	public Collection<Thread> getThreads()
	{
		return active;
	}
}
