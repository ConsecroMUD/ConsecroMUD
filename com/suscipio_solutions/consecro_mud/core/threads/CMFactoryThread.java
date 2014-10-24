package com.suscipio_solutions.consecro_mud.core.threads;



public class CMFactoryThread extends Thread
{
	private volatile Runnable runnable = null;

	public CMFactoryThread(ThreadGroup group, Runnable runnable, String name)
	{
		super(group,runnable,name);
		if(group==null) throw new java.lang.IllegalArgumentException();
		//this.runnable=runnable; the factory does not send a REAL runnable
	}

	/**
	 * Sets the runnable currently running
	 * if available
	 * @param runnable the runnable running
	 */
	public void setRunnable(Runnable runnable)
	{
		this.runnable=runnable;
	}

	/**
	 * Returns the runnable currently running
	 * if available
	 * @return the runnable running
	 */
	public Runnable getRunnable()
	{
		return runnable;
	}
}
