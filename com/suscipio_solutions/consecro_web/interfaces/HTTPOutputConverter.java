package com.suscipio_solutions.consecro_web.interfaces;

import java.io.File;
import java.nio.ByteBuffer;

import com.suscipio_solutions.consecro_web.http.HTTPException;
import com.suscipio_solutions.consecro_web.http.HTTPStatus;
import com.suscipio_solutions.consecro_web.util.CWConfig;



/**
 * Interface for any class that can convert an HTML output buffer 
 * for the web server to send to clients.  Includes some helpful 
 * constants that are often used in common http requests.  Works
 * by calling convertOutput to convert the input and calling
 * generateOutput to get the new output.
 
 *
 */
public interface HTTPOutputConverter
{

	/**
	 * Standard method for converting an intput buffer for writing to 
	 * the client.   The position and limit of the bytebuffer must
	 * already be set for reading the content.
	 * Call generateOutput() to get the new output.
	 * @param config the http configuration
	 * @param request the http request bring processed
	 * @param status the status of the request (so far)
	 * @param buffer the input buffer
	 * @param pageFile the file whose data is being converted
	 * @return the output buffer
	 * @throws HTTPException
	 */
	public ByteBuffer convertOutput(CWConfig config, HTTPRequest request, File pageFile, HTTPStatus status, ByteBuffer buffer) throws HTTPException;
}
