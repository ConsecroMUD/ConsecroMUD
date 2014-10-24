package com.suscipio_solutions.consecro_web.servlets;

import java.io.IOException;

import com.suscipio_solutions.consecro_web.http.HTTPMethod;
import com.suscipio_solutions.consecro_web.http.HTTPStatus;
import com.suscipio_solutions.consecro_web.http.MIMEType;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServlet;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletRequest;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletResponse;



/**
 * Purely for testing POST of form or urlencoded data
 
 *
 */
public class FormTesterServlet implements SimpleServlet
{

	@Override
	public void doGet(SimpleServletRequest request, SimpleServletResponse response)
	{
		response.setStatusCode(HTTPStatus.S405_METHOD_NOT_ALLOWED.getStatusCode());
	}

	@Override
	public void doPost(SimpleServletRequest request, SimpleServletResponse response)
	{
		try
		{
			response.setMimeType(MIMEType.html.getType());
			response.getOutputStream().write("<html><body><h1>Form Field Values</h1><br>".getBytes());
			for(final String cookieName : request.getCookieNames())
				response.getOutputStream().write(("Cookie \""+cookieName+"\": "+request.getCookie(cookieName)+"<br>").getBytes());
			for(final String field : request.getUrlParameters())
				response.getOutputStream().write(("Url Field \""+field+"\": "+request.getUrlParameter(field)+"<br>").getBytes());
			response.getOutputStream().write("</body></html>".getBytes());
		}
		catch (final IOException e)
		{
			response.setStatusCode(500);
		}
	}

	@Override
	public void init()
	{
	}

	@Override
	public void service(HTTPMethod method, SimpleServletRequest request, SimpleServletResponse response)
	{
		if(method != HTTPMethod.POST)
			response.setStatusCode(HTTPStatus.S405_METHOD_NOT_ALLOWED.getStatusCode());
	}

}
