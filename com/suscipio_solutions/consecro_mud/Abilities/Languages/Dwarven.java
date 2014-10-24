package com.suscipio_solutions.consecro_mud.Abilities.Languages;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Dwarven extends StdLanguage
{
	@Override public String ID() { return "Dwarven"; }
	private final static String localizedName = CMLib.lang().L("Dwarven");
	@Override public String name() { return localizedName; }
	public static List<String[]> wordLists=null;
	public Dwarven()
	{
		super();
	}

	@Override
	public List<String[]> translationVector(String language)
	{
		if(wordLists==null)
		{
			final String[] one={"o"};
			final String[] two={"`ai","`oi","`ul"};
			final String[] three={"aya","dum","mim","oyo","tum"};
			final String[] four={"menu","bund","ibun","khim","nala","rukhs","dumu","zirik","gunud","gabil","gamil"};
			final String[] five={"kibil","celeb","mahal","narag","zaram","sigin","tarag","uzbad","zigil","zirak","aglab","baraz","baruk","bizar","felak"};
			final String[] six={"azanul","bundushathur","morthond","felagund","gabilan","ganthol","khazad","kheled","khuzud","mazarbul","khuzdul"};
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
