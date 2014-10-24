package com.suscipio_solutions.consecro_web.interfaces;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Iterator;
import java.util.List;



/**
 * Manages multibyte byte buffers as an interator objects
 
 *
 */
public interface DataBuffers extends Iterator<ByteBuffer>
{

	/**
	 * Close out and clear all internal data buffers
	 * This is a required operation, which is done automatically
	 * if all data is iterated through, but should be done
	 * manually otherwise. 
	 */
	public void close();
	
	
	/**
	 * Flushes all internet bytebuffers to a single one.
	 * This can be an expensive operation, but will call
	 * close() on completion.
	 * @return these buffers flushed to one
	 */
	public ByteBuffer flushToBuffer();
	
	/**
	 * Return the length of all bytes buffers here
	 * @return an overall size
	 */
	public long getLength();

	/**
	 * Return the last modified date of the data content
	 * @return a date
	 */
	public Date getLastModified();

	/**
	 * Add a new ByteBuffer to this set.
	 * @param buf the buffer to add
	 * @param lastModifiedTime the last modified date of the data, or 0 to ignore
	 * @param isChunkable true if this is a body buffer, and can be chunked
	 */
	public void add(final ByteBuffer buf, final long lastModifiedTime, final boolean isChunkable);

	/**
	 * Add a new byte array to this set.
	 * @param buf the byte array to add
	 * @param lastModifiedTime the last modified date of the data, or 0 to ignore
	 * @param isChunkable true if this is a body buffer, and can be chunked
	 */
	public void add(final byte[] buf, final long lastModifiedTime, final boolean isChunkable);

	/**
	 * Splits the top buffer into two smaller buffers, with the new top buffer
	 * being the first part, and the new next-top buffer being the remainder.
	 * @param sizeOfTopBuffer the size of the top part of the buffer 0-this
	 * @return the new top bytebuffer
	 */
	public ByteBuffer splitTopBuffer(final int sizeOfTopBuffer);
	
	/**
	 * Add a new ByteBuffer to the top of this set.
	 * @param buf the buffer to add
	 * @param lastModifiedTime the last modified date of the data, or 0 to ignore
	 * @param isChunkable true if this is a body buffer, and can be chunked
	 */
	public void insertTop(final ByteBuffer buf, final long lastModifiedTime, final boolean isChunkable);
	
	/**
	 * Add a new random access file to this beginning of this set.
	 * @param file the input stream to add
	 * @param lastModifiedTime the last modified date of the data, or 0 to ignore
	 * @param isChunkable true if this is a body buffer, and can be chunked
	 */
	public void insertTop(final RandomAccessFile file, final long lastModifiedTime, final boolean isChunkable);
	
	/**
	 * Add a new random access file to this set.
	 * @param file the input stream to add
	 * @param lastModifiedTime the last modified date of the data, or 0 to ignore
	 * @param isChunkable true if this is a body buffer, and can be chunked
	 */
	public void add(final RandomAccessFile file, final long lastModifiedTime, final boolean isChunkable);

	/**
	 * Add a new byte array to the top of this set.
	 * @param buf the byte array to add
	 * @param lastModifiedTime the last modified date of the data, or 0 to ignore
	 * @param isChunkable true if this is a body buffer, and can be chunked
	 */
	public void insertTop(final byte[] buf, final long lastModifiedTime, final boolean isChunkable);
	
	/**
	 * Reconfigure this databuffer to return the given ranges
	 * Each range is 2 dimentional int array, startpos, endpos.
	 * @param ranges the list of ranges
	 */
	public void setRanges(final List<long[]> ranges);
	
	/**
	 * Reconfigure this databuffer to return chunks of the given
	 * size (or smaller) from the body, but only from buffers marked
	 * as chunkable.  Defaults 0.
	 * @param chunkSize a number > 0 to on chunking, 0 to turn off.
	 */
	public void setChunked(final int chunkSize);
}
