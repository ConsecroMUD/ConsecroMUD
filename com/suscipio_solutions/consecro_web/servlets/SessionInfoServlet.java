package com.suscipio_solutions.consecro_web.servlets;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import com.suscipio_solutions.consecro_web.http.HTTPMethod;
import com.suscipio_solutions.consecro_web.http.HTTPStatus;
import com.suscipio_solutions.consecro_web.http.MIMEType;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServlet;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletRequest;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletResponse;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletSession;



/**
 * Returns information about your servlet session in a page
 
 *
 */
public class SessionInfoServlet implements SimpleServlet
{

	@Override
	public void doGet(SimpleServletRequest request, SimpleServletResponse response)
	{
		try
		{
			response.setMimeType(MIMEType.html.getType());
			final SimpleServletSession session = request.getSession();
			response.getOutputStream().write("<html><body>".getBytes());
			response.getOutputStream().write(("<h1>Hello Session#"+session.getSessionId()+"</h1>").getBytes());
			final String lastTouch = DateFormat.getDateTimeInstance().format(new Date(session.getSessionLastTouchTime()));
			response.getOutputStream().write(("Last request was at: "+lastTouch+"<br>").getBytes());
			final String firstTouch = DateFormat.getDateTimeInstance().format(session.getSessionStart());
			response.getOutputStream().write(("First request was at: "+firstTouch+"<br>").getBytes());
			if(session.getUser().length()==0)
				session.setUser("BOB the "+this.hashCode());
			response.getOutputStream().write(("Your user name is: "+session.getUser()+"<br>").getBytes());
			response.getOutputStream().write("</body></html>".getBytes());
		}
		catch (final IOException e)
		{
			response.setStatusCode(500);
		}
	}

	@Override
	public void doPost(SimpleServletRequest request, SimpleServletResponse response)
	{
		response.setStatusCode(HTTPStatus.S405_METHOD_NOT_ALLOWED.getStatusCode());
	}

	@Override
	public void init()
	{
	}

	@Override
	public void service(HTTPMethod method, SimpleServletRequest request, SimpleServletResponse response)
	{
		if(method!=HTTPMethod.GET)
			response.setStatusCode(HTTPStatus.S405_METHOD_NOT_ALLOWED.getStatusCode());
	}

}
