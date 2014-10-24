package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.TickClient;


public interface CMLibrary extends CMObject
{
	public boolean activate();
	public boolean shutdown();
	public void propertiesLoaded();
	public TickClient getServiceClient();

	/**
	 * Localize an internal string -- shortcut. Same as calling:
	 * @see com.suscipio_solutions.consecro_mud.Libraries.interfaces.LanguageLibrary#fullSessionTranslation(String, String...)
	 * Call with the string to translate, which may contain variables of the form @x1, @x2, etc. The array in xs
	 * is then used to replace the variables AFTER the string is translated.
	 * @param str the string to translate
	 * @param xs the array of variables to replace
	 * @return the translated string, with all variables in place
	 */
	public String L(final String str, final String ... xs);
}
