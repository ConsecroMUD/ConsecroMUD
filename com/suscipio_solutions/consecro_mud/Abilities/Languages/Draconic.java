package com.suscipio_solutions.consecro_mud.Abilities.Languages;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Draconic extends StdLanguage
{
	@Override public String ID() { return "Draconic"; }
	private final static String localizedName = CMLib.lang().L("Draconic");
	@Override public String name() { return localizedName; }
	public static List<String[]> wordLists=null;
	public Draconic()
	{
		super();
	}

	@Override
	public List<String[]> translationVector(String language)
	{
		if(wordLists==null)
		{
			final String[] one={"y"};
			final String[] two={"ve","ov","iv","si","es","se"};
			final String[] three={"see","sev","ave","ces","ven","sod"};
			final String[] four={"nirg","avet","sav`e","choc","sess","sens","vent","vens","sven","yans","vays"};
			final String[] five={"splut","svets","fruite","dwagg","vrers","verrs","srens","swath","senys","varen"};
			final String[] six={"choccie","svenren","yorens","vyrues","whyrie","vrysenso","forin","sinnes","sessis","uroven","xorers","nosees"};
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
