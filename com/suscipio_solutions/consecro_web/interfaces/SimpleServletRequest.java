package com.suscipio_solutions.consecro_web.interfaces;

import java.util.logging.Logger;



/**
 * This interface encapsulates the HTTP request portion 
 * of the SimpleServlet specification.
 */
public interface SimpleServletRequest extends HTTPRequest 
{
	/**
	 * Access the server who accepted and is managing this
	 * request.
	 * @return the server who accepted and is managing this request
	 */
	public SimpleServletManager getServletManager();
	
	/**
	 * Returns the session object associated with this servlet request
	 * @return the session object
	 */
	public SimpleServletSession getSession();
	
	/**
	 * Returns a java.util.Logger-compliant logger to write to
	 * @return a logger
	 */
	public Logger getLogger();
}