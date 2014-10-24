package com.suscipio_solutions.consecro_web.http;



/**
 * Public enum type for all acceptable request types.
 * Most of these types are arbitrary, as only GET and HEAD
 * are directly supported by the web file server, and servlets
 * can do what they want.  In other words, expand this list at will.
 
 */
public enum HTTPMethod
{
	GET, HEAD, POST, PUT, DELETE, OPTIONS;
	
	public static String getAllowedList() 
	{ 
		return "GET, HEAD, POST, PUT, DELETE, OPTIONS"; 
	}
}
