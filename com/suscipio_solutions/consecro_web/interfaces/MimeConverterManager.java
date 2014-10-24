package com.suscipio_solutions.consecro_web.interfaces;

import java.util.Collection;

import com.suscipio_solutions.consecro_web.http.MIMEType;
import com.suscipio_solutions.consecro_web.util.RequestStats;



/**
 * Interface for an http response converter manager, based on the mime type 
 * of the file returned.
 * 
 
 *
 */
public interface MimeConverterManager
{
	/**
	 * Internal method to register a servlets existence, and its context.
	 * This will go away when a config file is permitted
	 * @param context the uri context the servlet responds to
	 * @param converterClass the class of the converter
	 */
	public void registerConverter(MIMEType mime, Class<? extends HTTPOutputConverter> converterClass);
	
	/**
	 * For anyone externally interested, will return the list of converter classes
	 * that are registered
	 * @return the list of converter classes
	 */
	public Collection<Class<? extends HTTPOutputConverter>> getConverters();

	/**
	 * Returns a converter (if any) that handles the given mime type.
	 * if none is found, NULL is returned.
	 * @param mime the mime type
	 * @return the servlet class, if any, or null
	 */
	public Class<? extends HTTPOutputConverter> findConverter(MIMEType mime);

	/**
	 * Returns a statistics object for the given converter class
	 * or null if none exists
	 * @param converterClass the converter class managed by this web server
	 * @return the converter stats object
	 */
	public RequestStats getConverterStats(Class<? extends HTTPOutputConverter> converterClass);
}
