package com.suscipio_solutions.consecro_web.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.suscipio_solutions.consecro_web.http.HTTPMethod;
import com.suscipio_solutions.consecro_web.http.HTTPStatus;
import com.suscipio_solutions.consecro_web.http.MIMEType;
import com.suscipio_solutions.consecro_web.http.MultiPartData;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServlet;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletRequest;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletResponse;



/**
 * Relies on multi-part form data to write all received files to the current
 * web server directory.
 
 *
 */
public class MultipartFileWriter implements SimpleServlet
{

	@Override
	public void doGet(SimpleServletRequest request, SimpleServletResponse response)
	{
		response.setStatusCode(HTTPStatus.S405_METHOD_NOT_ALLOWED.getStatusCode());
	}

	private void writeFilesFromParts(List<MultiPartData> parts, StringBuilder filesList) throws IOException
	{
		if(parts != null)
		{
			for(final MultiPartData part : parts)
			{
				final String filename = part.getVariables().get("filename");
				if(filename != null)
				{
					final File f = new File(filename);
					FileOutputStream fout=null;
					try
					{
						fout=new FileOutputStream(f);
						fout.write(part.getData());
						filesList.append(filename).append("<br>");
					}
					finally
					{
						if(fout!=null)
							fout.close();
					}
				}
				writeFilesFromParts(part.getSubParts(), filesList);
			}
		}
	}
	
	@Override
	public void doPost(SimpleServletRequest request, SimpleServletResponse response)
	{
		try
		{
			final StringBuilder filesList=new StringBuilder("");
			response.setMimeType(MIMEType.html.getType());
			writeFilesFromParts(request.getMultiParts(), filesList);
			response.getOutputStream().write(("<html><body><h1>Done</h1><br>"+filesList.toString()+"</body></html>").getBytes());
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
