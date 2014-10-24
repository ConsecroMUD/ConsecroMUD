package com.suscipio_solutions.consecro_mud.Abilities.Languages;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Orcish extends StdLanguage
{
	@Override public String ID() { return "Orcish"; }
	private final static String localizedName = CMLib.lang().L("Orcish");
	@Override public String name() { return localizedName; }
	public static List<String[]> wordLists=null;
	public Orcish()
	{
		super();
	}
	@Override
	public List<String[]> translationVector(String language)
	{
		if(wordLists==null)
		{
			final String[] one={"a"};
			final String[] two={"uk","ik","og","eg","ak","ag"};
			final String[] three={"uko","ugg","ick","ehk","akh","oog"};
			final String[] four={"blec","mugo","guck","gook","kill","dead","twak","kwat","klug"};
			final String[] five={"bleko","thwak","klarg","gluck","kulgo","mucka","splat","kwath","garth","blark"};
			final String[] six={"kalarg","murder","bleeke","kwargh","guttle","thungo"};
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
