package com.suscipio_solutions.consecro_web.servlets;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import com.suscipio_solutions.consecro_web.http.HTTPHeader;
import com.suscipio_solutions.consecro_web.http.HTTPMethod;
import com.suscipio_solutions.consecro_web.http.HTTPStatus;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServlet;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletRequest;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletResponse;



/**
 * Returns information about your web server in a page
 
 *
 */
public class FormLoggerServlet implements SimpleServlet
{

	@Override
	public void doGet(SimpleServletRequest request, SimpleServletResponse response)
	{
		response.setStatusCode(HTTPStatus.S405_METHOD_NOT_ALLOWED.getStatusCode());
	}

	@Override
	public void doPost(SimpleServletRequest request, SimpleServletResponse response)
	{
		request.getLogger().info(" vvv-------------------- PayloadLogger ----------------------vvv");
		request.getLogger().info("Request: "+request.getFullRequest());
		for(String field : request.getUrlParameters())
			request.getLogger().info("Url Field \""+field+"\": "+request.getUrlParameter(field));
		int contentLength = 0;
		try {
			contentLength = Integer.parseInt(request.getHeader(HTTPHeader.CONTENT_LENGTH.lowerCaseName()));
		} catch (Exception e) { }
		if(contentLength > 0) {
			try {
				Reader bodyReader = new InputStreamReader(request.getBody());
				char[] buf = new char[contentLength];
				bodyReader.read(buf);
				request.getLogger().info("Body: "+new String(buf));
			} catch (IOException e) { }
		}
		request.getLogger().info(" ^^^-------------------- PayloadLogger ----------------------^^^");
	}

	@Override
	public void init()
	{
	}

	@Override
	public void service(HTTPMethod method, SimpleServletRequest request, SimpleServletResponse response)
	{
		if(method!=HTTPMethod.POST)
			response.setStatusCode(HTTPStatus.S405_METHOD_NOT_ALLOWED.getStatusCode());
	}

}
