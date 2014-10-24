package com.suscipio_solutions.consecro_web.util;

import java.util.HashSet;
import java.util.Set;

import com.suscipio_solutions.consecro_web.http.MIMEType;



/**
 * A wrapper for chunked encoding specification
 
 *
 */
public class ChunkSpec
{
	private final	int				chunkSize;
	private final	Set<MIMEType>	mimeTypes;
	private final	long			minFileSize;

	/**
	 * Create a ChunkSpec object for a particular path/domain. 
	 * @param chunkSize the default size for each chunk, or smaller
	 * @param mimeTypes null for all mimetypes, or a list of allowed types
	 * @param minFileSize the minimum payload size to produce chunking, or 0 for all
	 */
	public ChunkSpec(int chunkSize, Set<MIMEType> mimeTypes, long minFileSize)
	{
		this.chunkSize = chunkSize;
		this.mimeTypes = new HashSet<MIMEType>();
		this.mimeTypes.addAll(mimeTypes);
		this.minFileSize = minFileSize;
	}

	public int getChunkSize() 
	{
		return chunkSize;
	}

	public Set<MIMEType> getMimeTypes() 
	{
		return mimeTypes;
	}

	public long getMinFileSize() 
	{
		return minFileSize;
	}
}
