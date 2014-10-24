package com.suscipio_solutions.consecro_web.interfaces;

import java.io.File;

import com.suscipio_solutions.consecro_web.http.HTTPException;



/**
 * Retreives Web Server File Data
 
 *
 */
public interface HTTPFileGetter 
{
	/**
	 * Generates a bytebuffer representing the results of the request 
	 * contained herein.  HTTP errors can still be generated, however,
	 * so those are watched for.
	 * 
	 * Requests can trigger file reads, servlet calls and other ways
	 * of generating body and header data.
	 * 
	 * @param request the request to generate output for
	 * @throws HTTPException
	 * @return the entire full output for this request
	 */
	public DataBuffers generateOutput(HTTPRequest request) throws HTTPException;
	
	/**
	 * Retreives a buffer set containing the possibly cached contents of the file. 
	 * This can trigger file reads, servlet calls and other ways
	 * of generating body data.
	 * 
	 * @param request the request to generate output for
	 * @throws HTTPException
	 * @return the entire full output for this request
	 */
	public DataBuffers getFileData(HTTPRequest request) throws HTTPException;
	
	/**
	 * After a uri has been broken apart and inspected, this method is called
	 * to reassemble it into a valid File path using local file separators.
	 * If you wish to add a special directory root for html docs, this would
	 * be the appropriate place to do it.
	 * @param request the request being processed
	 * @return the full assembled file
	 */
	public File assembleFileRequest(HTTPRequest request);
}
