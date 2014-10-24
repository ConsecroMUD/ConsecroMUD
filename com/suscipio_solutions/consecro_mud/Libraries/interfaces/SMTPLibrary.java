package com.suscipio_solutions.consecro_mud.Libraries.interfaces;

import java.io.IOException;
import java.net.UnknownHostException;

import com.suscipio_solutions.consecro_mud.core.exceptions.BadEmailAddressException;


public interface SMTPLibrary extends CMLibrary
{
	/** Default port number */
	public static final int DEFAULT_PORT = 25;
	/** network end of line */
	public static final String EOL = "\r\n";
	/** default timeout */
	public static final int DEFAULT_TIMEOUT=10000;

	public boolean isValidEmailAddress(String addy);
	public boolean emailIfPossible(String SMTPServerInfo, String from, String replyTo, String to, String subject, String message)
		throws IOException;
	public boolean emailIfPossible(String fromName, String toName, String subj, String msg);
	public void emailOrJournal(String SMTPServerInfo, String from, String replyTo, String to, String subject, String message);
	public SMTPClient getClient(String SMTPServerInfo, int port)  throws UnknownHostException, IOException;
	public SMTPClient getClient(String emailAddress) throws IOException, BadEmailAddressException;

	public static interface SMTPClient
	{
		public void sendMessage(String froaddress, String reply_address, String to_address, String mockto_address, String subject, String message)
			throws IOException;
	}
}
