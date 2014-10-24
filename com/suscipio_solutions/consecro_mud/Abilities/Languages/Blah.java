package com.suscipio_solutions.consecro_mud.Abilities.Languages;
import java.util.List;

import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Blah extends StdLanguage
{
	@Override public String ID() { return "Blah"; }
	private final static String localizedName = CMLib.lang().L("Blah");
	@Override public String name() { return localizedName; }
	public static List<String[]> wordLists=null;
	private static boolean mapped=false;
	public Blah()
	{
		super();
		if(!mapped){mapped=true;
					CMLib.ableMapper().addCharAbilityMapping("Immortal",1,ID(),false);}
	}

	@Override
	public List<String[]> translationVector(String language)
	{
		return wordLists;
	}

	@Override
	public String translate(String language, String word)
	{
		return fixCase(word,"blah");
	}
}
