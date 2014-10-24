package com.suscipio_solutions.consecro_web.http;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;
import com.suscipio_solutions.consecro_web.interfaces.ServletSessionManager;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletSession;
import com.suscipio_solutions.consecro_web.util.CWConfig;



/**
 * This class manages servlet session objects
 * for servlets as well.
 * 
 
 *
 */
public class SessionManager implements ServletSessionManager
{
	private final Map<String,SimpleServletSession>	sessions;		// map of ids to sessions
	private final CWConfig						config;
	
	/**
	 * Construct a session manager
	 * @param config the web server config
	 */
	public SessionManager(CWConfig config)
	{
		sessions = new Hashtable<String, SimpleServletSession>();  
		this.config=config;
	}
	
	/**
	 * Internal method to find an existing session based on the request data.
	 * @param sessionID the id of the session
	 */
	@Override
	public SimpleServletSession findSession(String sessionID)
	{
		return sessions.get(sessionID);
	}

	/**
	 * Internal method to find an existing session based on the request data.
	 * If the session does not exist, it will be created and returned
	 * @param sessionID the id of the session
	 */
	@Override
	public SimpleServletSession findOrCreateSession(String sessionID)
	{
		SimpleServletSession session = sessions.get(sessionID);
		if(session != null) return session;
		session = new ServletSession(sessionID);
		synchronized(sessions)
		{
			sessions.put(sessionID, session);
			return session;
		}
	}
	
	/**
	 * A maintence method forcing the manager to examine all sessions
	 * for any that have timed out and remove them, if so.
	 * @return the list of servlet classes
	 */
	@Override
	public void cleanUpSessions()
	{
		synchronized(sessions)
		{
			final long currentTime=System.currentTimeMillis();
			final long idleExpireTime=currentTime - config.getSessionMaxIdleMs();
			final Date ageExpireTime=new Date(currentTime - config.getSessionMaxAgeMs());
			for(final Iterator<String> s=sessions.keySet().iterator();s.hasNext();)
			{
				final String sessionID=s.next();
				final SimpleServletSession session=sessions.get(sessionID);
				if((session.getSessionLastTouchTime() < idleExpireTime)
				||(session.getSessionStart().before(ageExpireTime)))
				{
					s.remove();
				}
			}
		}
	}

	/**
	 * For generating a new servlet session and returning its ID
	 * @param request the current request to base the new session on
	 * @return the new servlet session obj
	 */
	@Override
	public SimpleServletSession createSession(HTTPRequest request)
	{
		String sessionID = request.getClientAddress().hashCode()+""+System.currentTimeMillis() + "" + System.nanoTime();
		try
		{
			while(sessions.containsKey(sessionID))
			{
				Thread.sleep(1);
				sessionID = request.getClientAddress().hashCode()+""+System.currentTimeMillis() + "" + System.nanoTime();
			}
		}catch(final Exception e)
		{
			config.getLogger().throwing("", "", e);
		}
		final SimpleServletSession newSession = new ServletSession(sessionID);
		synchronized(sessions)
		{
			sessions.put(sessionID, newSession);
			return newSession;
		}
	}
}
