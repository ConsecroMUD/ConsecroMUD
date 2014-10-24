package com.suscipio_solutions.consecro_mud.WebMacros.interfaces;
import com.suscipio_solutions.consecro_mud.core.exceptions.HTTPServerException;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;
import com.suscipio_solutions.consecro_web.interfaces.SimpleServletResponse;


/**
 * Web Macros are special commands which can be inserted into coffeemud web
 * page (cmvp) files, and can have those command strings substituted with
 * calculated results.  They can include parameters, and can access the
 * other URL parameters.
 */
public interface WebMacro extends CMObject
{
	/**
	 * The public name of this macro
	 * @return The public name of this macro
	 */
	@Override
	public String name();
	/**
	 * Whether the runMacro or runBinaryMacro executor should be called.
	 * @see WebMacro#runBinaryMacro(HTTPRequest, String)
	 * @see WebMacro#runMacro(HTTPRequest, String)
	 * @return whether the runBinaryMacro executor should be called instead of runMacro
	 */
	public boolean preferBinary();
	/**
	 * Whether this macro is restricted to the admin web server.
	 * @return true if the macro is restricted to the admin web server
	 */
	public boolean isAdminMacro();

	/**
	 * Whether this macro returns an attachment instead of something
	 * displayable.  If true, the content-disposition will reflect
	 * the filename parameter, and any other header or other response
	 * settings may be embedded here.
	 * @see WebMacro#getFilename(HTTPRequest, String)
	 * @param response the WebServer servlet response object
	 * @param filename the filename from getFilename
	 */
	public void setServletResponse(SimpleServletResponse response, final String filename);

	/**
	 * Whether this macro substitutes as an aspect of the web path instead
	 * of a standard web macro.  If true is returned, URLs such as:
	 * http://mydomain.com/mymacroname?firstparm=value&secondparm=value
	 * might succeeed
	 * @see WebMacro#getFilename(HTTPRequest, String)
	 * @return whether this is a wierd URL macro
	 */
	public boolean isAWebPath();
	/**
	 * If this macro returns true from isAWebPath(), this will be the substitute
	 * filename to use as a page for returning to the caller.  It may simply
	 * return what is given to it.
	 * @see WebMacro#isAWebPath()
	 * @see com.suscipio_solutions.consecro_web.interfaces.HTTPRequest
	 * @param httpReq the requests object
	 * @param filename the default filename
	 * @return usually the default filename again
	 */
	public String getFilename(HTTPRequest httpReq, String filename);

	/**
	 * This method is executed only if this macro returns true for preferBinary().
	 * It will execute the macro and return its results as a binary byte array.
	 * @see WebMacro#preferBinary()
	 * @see com.suscipio_solutions.consecro_web.interfaces.HTTPRequest
	 * @param httpReq the external requests object
	 * @param parm any parameter strigs given to the macro
	 * @return the binary stream result of running this macro
	 * @throws HTTPServerException
	 */
	public byte[] runBinaryMacro(HTTPRequest httpReq, String parm) throws HTTPServerException;
	/**
	 * This method is executed only if this macro returns false for preferBinary().
	 * It will execute the macro and return its results as a string, which is then
	 * substituted for the macro reference in the web page where the macro was found.
	 * @see WebMacro#preferBinary()
	 * @see com.suscipio_solutions.consecro_web.interfaces.HTTPRequest
	 * @param httpReq the external requests object
	 * @param parm any parameter strigs given to the macro
	 * @return the string result of running this macro
	 * @throws HTTPServerException
	 */
	public String runMacro(HTTPRequest httpReq, String parm) throws HTTPServerException;
}
