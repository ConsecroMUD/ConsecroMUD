package com.suscipio_solutions.consecro_web.servlets;

import java.io.IOException;

import com.suscipio_solutions.consecro_web.http.HTTPMethod;
import com.suscipio_solutions.consecro_web.http.HTTPStatus;
import com.suscipio_solutions.consecro_web.http.MIMEType;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServlet;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletRequest;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletResponse;
import com.suscipio_solutions.consecro_web.util.RequestStats;



/**
 * Displays statistics kept about servlet calls and performance
 
 *
 */
public class ServletStatsServlet implements SimpleServlet
{

	private void appendStats(RequestStats stats, StringBuilder str)
	{
		str.append("Requests total: ").append(stats.getNumberOfRequests()).append("<br>");
		str.append("Average time (ns): ").append(stats.getAverageEllapsedNanos()).append("<br>");
		str.append("In progress: ").append(stats.getNumberOfRequestsInProcess()).append("<br>");
	}
	
	@Override
	public void doGet(SimpleServletRequest request, SimpleServletResponse response)
	{
		try
		{
			response.setMimeType(MIMEType.html.getType());
			final StringBuilder str = new StringBuilder("");
			str.append("<html><body>");
			
			RequestStats stats;
			for(final Class<? extends SimpleServlet> servletClass : request.getServletManager().getServlets())
			{
				stats = request.getServletManager().getServletStats(servletClass);
				str.append("<P><h2>"+servletClass.getSimpleName()+"</h2></p><br>");
				appendStats(stats, str);
			}
			str.append("</body></html>");
			response.getOutputStream().write(str.toString().getBytes());
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
