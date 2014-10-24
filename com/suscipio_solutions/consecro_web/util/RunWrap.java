package com.suscipio_solutions.consecro_web.util;



/**
 * A runnable wrapper to be used with CWThreadExecutor that tracks
 * its own active running time.
 
 *
 */
public class RunWrap
{
	private final Runnable runnable;
	private final Thread   thread;
	private final long 	   startTime;
	
	public RunWrap(Runnable runnable, Thread thread)
	{
		this.runnable=runnable;
		this.thread=thread;
		startTime=System.currentTimeMillis();
	}
	
	public Runnable getRunnable()
	{
		return runnable;
	}
	
	public Thread getThread()
	{
		return thread;
	}
	
	/**
	 * Returns the number of milliseconds this runnable
	 * has been running.
	 * @return the time in millis
	 */
	public long activeTimeMillis()
	{
		return System.currentTimeMillis()-startTime;
	}
}
