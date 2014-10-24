package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.Map;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;

public interface TelnetFilter extends CMLibrary
{
	public final static String hexStr="0123456789ABCDEF";

	public enum Pronoun
	{
		HISHER("-HIS-HER","-h"),
		HIMHER("-HIM-HER","-m"),
		NAME("-NAME",null),
		NAMESELF("-NAMESELF","-s"),
		HESHE("-HE-SHE","-e"),
		ISARE("-IS-ARE",null),
		HASHAVE("-HAS-HAVE",null),
		YOUPOSS("-YOUPOSS","`s"),
		HIMHERSELF("-HIM-HERSELF","-ms"),
		HISHERSELF("-HIS-HERSELF","-hs"),
		SIRMADAM("-SIRMADAM",null),
		ISARE2("IS-ARE",null),
		NAMENOART("-NAMENOART",null),
		ACCOUNTNAME("-ACCOUNTNAME",null);
		public final String suffix;
		public final String emoteSuffix;
		private Pronoun(String suffix, String emoteSuffix)
		{
			this.suffix=suffix;
			this.emoteSuffix=emoteSuffix;
		}
	}

	public Map<String, Pronoun> getTagTable();
	public String simpleOutFilter(String msg);
	// no word-wrapping, text filtering or ('\','n') -> '\n' translations
	// (it's not a member of the interface either so probably shouldn't be public)
	public String colorOnlyFilter(String msg, Session S);
	public String[] wrapOnlyFilter(String msg, int wrap);
	public String getLastWord(StringBuffer buf, int lastSp, int lastSpace);
	public String fullOutFilter(Session S, MOB mob, Physical source, Environmental target, Environmental tool, String msg, boolean wrapOnly);
	public String simpleInFilter(StringBuilder input, boolean permitMXPTags);
	public String simpleInFilter(StringBuilder input);
	public String fullInFilter(String input);
	public String safetyFilter(String s);
}
