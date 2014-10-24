package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.Map;

import com.suscipio_solutions.consecro_mud.core.exceptions.HTTPRedirectException;
import com.suscipio_solutions.consecro_web.interfaces.HTTPOutputConverter;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;

public interface WebMacroLibrary extends CMLibrary, HTTPOutputConverter
{
	public byte [] virtualPageFilter(byte [] data) throws HTTPRedirectException;
	public String virtualPageFilter(String s) throws HTTPRedirectException;
	public StringBuffer virtualPageFilter(StringBuffer s) throws HTTPRedirectException;
	public StringBuffer virtualPageFilter(HTTPRequest request, Map<String, Object> objects, long[] processStartTime, String[] lastFoundMacro, StringBuffer s) throws HTTPRedirectException;
	public String clearWebMacros(StringBuffer s);
	public String parseFoundMacro(StringBuffer s, int i, boolean lookOnly);
	public String clearWebMacros(String s);
	public String copyYahooGroupMsgs(String user, String password, String url, int numTimes, int[] skipList, String journal);
}
