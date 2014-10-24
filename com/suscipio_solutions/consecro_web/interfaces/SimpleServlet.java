package com.suscipio_solutions.consecro_web.interfaces;

import com.suscipio_solutions.consecro_web.http.HTTPMethod;



/**
 * This is the basic interface that "simple servlets" are provided, 
 * providing access to the request data and an interface to control
 * the server response.
 */
public interface SimpleServlet 
{
	public void init();
	public void doGet(SimpleServletRequest request, SimpleServletResponse response);
	public void doPost(SimpleServletRequest request, SimpleServletResponse response);
	public void service(HTTPMethod method, SimpleServletRequest request, SimpleServletResponse response);
}
 
