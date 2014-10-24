package com.suscipio_solutions.consecro_web.interfaces;

import java.io.File;

import com.suscipio_solutions.consecro_web.http.HTTPException;



/**
 * Manages FileCache objects
 
 *
 */
public interface FileCacheManager
{
	public static enum CompressionType {NONE, GZIP, DEFLATE}
	
	/**
	 * The publically accessible method for getting data from a file (or
	 * potentially from the cache.  You can also pass in a one dimensional
	 * eTay holder.  If the holder contains a valid tag that matches the
	 * file requested, a 304 not modified exception is thrown.  Otherwise,
	 * the holder is populated with the valid eTag when the byte[] buffer
	 * is returned.
	 * @param pageFile the local file to fetch
	 * @param eTag the r/w eTag holder, a one dimensional string array
	 * @return the byte[] buffer of the file to send to the client
	 * @throws HTTPException either 304 or 404
	 */
	public DataBuffers getFileData(File pageFile, final String[] eTag) throws HTTPException;
	
	/**
	 * The publically accessible method for either compressing file data, or
	 * potentially from the cache. 
	 * @param filename the name of the file that is being compressed
	 * @param type the type of compression to look for
	 * @return the byte[] buffer of the file to compress
	 * @throws HTTPException either 304 or 404
	 */
	public DataBuffers compressFileData(final File pageFile, final CompressionType type, final DataBuffers uncompressedBytes) throws HTTPException;
}
