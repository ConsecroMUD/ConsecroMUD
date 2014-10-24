package com.suscipio_solutions.consecro_web.interfaces;



/**
 * Interface for session managers for the web server.  Maintains a cache
 * of sessionID->session object map, and includes a method for periodic
 * timeout/cleanup which should be called from time to time for memory
 * purposes.
 
 */
public interface ServletSessionManager
{
	/**
	 * Internal method to find an existing session based on the request data.
	 * @param sessionID the id of the session
	 */
	public SimpleServletSession findSession(String sessionID);
	
	/**
	 * Internal method to find an existing session based on the request data.
	 * If the session does not exist, it will be created and returned
	 * @param sessionID the id of the session
	 */
	public SimpleServletSession findOrCreateSession(String sessionID);
	
	/**
	 * For generating a new servlet session and returning its ID
	 * @param request the current request to base the new session on
	 * @return the new servlet session obj
	 */
	public SimpleServletSession createSession(HTTPRequest request);

	/**
	 * A maintence method forcing the manager to examine all sessions
	 * for any that have timed out and remove them, if so.
	 * @return the list of servlet classes
	 */
	public void cleanUpSessions();
}
