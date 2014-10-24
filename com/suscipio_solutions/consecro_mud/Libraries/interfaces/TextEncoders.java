package com.suscipio_solutions.consecro_mud.Libraries.interfaces;

public interface TextEncoders extends CMLibrary
{
	public String decompressString(byte[] b);
	public byte[] compressString(String s);
	public boolean checkAgainstRandomHashString(final String checkString, final String hashString);
	public boolean isARandomHashString(final String password);
	public String makeRandomHashString(final String password);
	public String generateRandomPassword();
}
