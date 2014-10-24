package com.suscipio_solutions.consecro_mud.core.threads;

public class TimeMs
{
	private long time;

	public TimeMs(long t)
	{
		this.time=t;
	}

	public TimeMs()
	{
		this.time=System.currentTimeMillis();
	}

	public synchronized void setToNow()
	{
		this.time=System.currentTimeMillis();
	}

	public synchronized void setToLater(long amount)
	{
		this.time=System.currentTimeMillis()+amount;
	}

	public synchronized void set(long t)
	{
		this.time=t;
	}

	public synchronized long get()
	{
		return this.time;
	}

	public synchronized boolean isNowLaterThan()
	{
		return System.currentTimeMillis() > this.time;
	}

	public synchronized boolean isNowEarlierThan()
	{
		return System.currentTimeMillis() < this.time;
	}
}
