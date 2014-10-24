package com.suscipio_solutions.consecro_mud.Abilities.Languages;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Gnomish extends StdLanguage
{
	@Override public String ID() { return "Gnomish"; }
	private final static String localizedName = CMLib.lang().L("Gnomish");
	@Override public String name() { return localizedName; }
	public static List<String[]> wordLists=null;
	public Gnomish()
	{
		super();
	}
	@Override
	public List<String[]> translationVector(String language)
	{
		if(wordLists==null)
		{
			final String[] one={"y"};
			final String[] two={"te","it","at","to"};
			final String[] three={"nep","tem","tit","nip","pop","pon","upo","wip","pin"};
			final String[] four={"peep","meep","neep","pein","nopo","popo","woop","weep","teep","teet"};
			final String[] five={"whemp","thwam","nippo","punno","upoon","teepe","tunno","ponno","twano","ywhap"};
			final String[] six={"tawhag","ponsol","paleep","ponpopol","niptittle","minwap","tinmipmip","niptemtem","wipwippoo"};
			wordLists=new Vector<String[]>();
			wordLists.add(one);
			wordLists.add(two);
			wordLists.add(three);
			wordLists.add(four);
			wordLists.add(five);
			wordLists.add(six);
		}
		return wordLists;
	}
}
