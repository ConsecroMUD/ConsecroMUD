package com.suscipio_solutions.consecro_web.util;



/**
 * POJ for tracking some basic statistics about servlets
 * Pretty self-explanatory
 
 *
 */
public class RequestStats
{
	private volatile int  requestsProcessed= 0;
	private volatile long requestTime	   = 0;
	private volatile int  requestsInProcess= 0;
	
	public synchronized void startProcessing()
	{
		requestsInProcess++;
	}
	
	public synchronized void endProcessing(long timeEllapsed)
	{
		requestsInProcess--;
		requestsProcessed++;
		requestTime+=timeEllapsed;
	}
	
	public int getNumberOfRequests()
	{
		return requestsProcessed;
	}
	
	public synchronized long getAverageEllapsedNanos()
	{
		if(requestsProcessed == 0)
			return 0;
		return requestTime / requestsProcessed;
	}
	
	public int getNumberOfRequestsInProcess()
	{
		return requestsInProcess;
	}
}
