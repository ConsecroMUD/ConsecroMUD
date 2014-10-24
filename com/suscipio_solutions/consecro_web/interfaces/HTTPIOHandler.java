package com.suscipio_solutions.consecro_web.interfaces;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.suscipio_solutions.consecro_web.http.HTTPHeader;
import com.suscipio_solutions.consecro_web.http.HTTPStatus;
import com.suscipio_solutions.consecro_web.server.WebServer;



/**
 * For off-thread async reading, this interface does well.  The runnable portion
 * ensures that nothing is read unless its given its own thread time (reading occurs
 * in the run() method iow).  The rest helps external entites manage or read its
 * internal state.
 * 
 * For now, the only IO handlers are readers, though in the future this same interface
 * would be great for writers.
 
 *
 */
public interface HTTPIOHandler extends Runnable
{
	public static final String 		 EOLN 			= "\r\n";			// standard EOLN for http protocol
	public static final String 		 SERVER_HEADER 	= HTTPHeader.SERVER.makeLine("CoffeeWebServer/"+WebServer.VERSION);
	public static final String 		 CONN_HEADER  	= HTTPHeader.CONNECTION.makeLine("Keep-Alive");
	public static final String 		 RANGE_HEADER  	= HTTPHeader.ACCEPT_RANGES.makeLine("bytes");
	public static final DateFormat 	 DATE_FORMAT	= new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss zzz");
	public static final byte[]		 CONT_RESPONSE  = ("HTTP/1.1 "+HTTPStatus.S100_CONTINUE.getStatusCode()+" CONTINUE" + EOLN + EOLN).getBytes(); 

	/**
	 * Returns the name of this handler.
	 * @return the name of this handler
	 */
	public String getName();
	
	/**
	 * Force the io handler to close itself off to any future activity
	 * If the runnable is running, wait until its done before returning
	 */
	public void closeAndWait();
	
	/**
	 * Returns whether this handler considers itself done.  If true is
	 * returned, the close() method should be called next, and then this
	 * object never touched again by its manager.
	 * @return true if this handler is done
	 */
	public boolean isCloseable();
	
	/**
	 * Returns true if this handler is currently, actively, processing 
	 * in another thread.  Can be used to prevent two separate threads from
	 * blocking on the same io channel
	 * @return true if the handler is active atm.
	 */
	public boolean isRunning();
	
	/**
	 * Reads bytes from the given buffer into the internal channel channel.
	 * @param buffer source buffer for the data write
	 * @return number of bytes written
	 * @throws IOException
	 */
	public int writeBlockingBytesToChannel(final DataBuffers buffer) throws IOException;
	
	/**
	 * Queues the given buffer for eventual writing to the channel
	 * @param buffer source buffer for the data write
	 * @throws IOException
	 */
	public void writeBytesToChannel(final DataBuffers buffer) throws IOException;
}
