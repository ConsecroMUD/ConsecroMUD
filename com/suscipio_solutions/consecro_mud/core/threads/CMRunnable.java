package com.suscipio_solutions.consecro_mud.core.threads;

public interface CMRunnable extends Runnable
{
	/**
	 * Returns the number of milliseconds this runnable
	 * has been running.
	 * @return the time in millis
	 */
	public long activeTimeMillis();
}
