package com.suscipio_solutions.consecro_web.servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.suscipio_solutions.consecro_web.http.HTTPException;
import com.suscipio_solutions.consecro_web.http.HTTPMethod;
import com.suscipio_solutions.consecro_web.http.HTTPStatus;
import com.suscipio_solutions.consecro_web.http.MIMEType;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServlet;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletRequest;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletResponse;



/**
 * Dumps the web server log into a page 
 
 *
 */
public class LogDumperServlet implements SimpleServlet
{

	@Override
	public void doGet(SimpleServletRequest request, SimpleServletResponse response)
	{
		try
		{
			response.setMimeType(MIMEType.html.getType());
			final File pageFile=new File("web.log");
			if((!pageFile.exists()) || (!pageFile.canRead()) || (pageFile.length() > Integer.MAX_VALUE))
				throw HTTPException.standardException(HTTPStatus.S404_NOT_FOUND);
			final byte[] fileBuf = new byte[(int)pageFile.length()];
			BufferedInputStream bs = null;
			try
			{
				bs=new BufferedInputStream(new FileInputStream(pageFile));
				bs.read(fileBuf);
				response.getOutputStream().write("<html><body><pre>".getBytes());
				response.getOutputStream().write(fileBuf);
				response.getOutputStream().write("</pre></body></html>".getBytes());
			}
			catch(final FileNotFoundException e)
			{
				request.getLogger().throwing("", "", e);
				// not quite sure how we could get here.
				throw HTTPException.standardException(HTTPStatus.S404_NOT_FOUND);
			}
			catch (final IOException e)
			{
				request.getLogger().throwing("", "", e);
				throw HTTPException.standardException(HTTPStatus.S404_NOT_FOUND);
			}
			finally
			{
				if(bs != null)
				{
					try
					{
						bs.close(); 
				}
					catch(final Exception e) {} // java really needs an " i don't care " syntax for exception handling
			}
		}
		}
		catch (final HTTPException e)
		{
			try
			{
				response.getOutputStream().write(e.generateOutput(request).flushToBuffer().array());
			}
			catch (final Exception e1){}
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
