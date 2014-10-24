package com.suscipio_solutions.consecro_mud.Abilities.Languages;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Druidic extends StdLanguage
{
	@Override public String ID() { return "Druidic"; }
	private final static String localizedName = CMLib.lang().L("Druidic");
	@Override public String name() { return localizedName; }
	public static List<String[]> wordLists=null;
	public Druidic()
	{
		super();
	}

	@Override
	public List<String[]> translationVector(String language)
	{
		if(wordLists==null)
		{
			final String[] one={""};
			final String[] two={"hissssss","hoo","caw","arf","bow-wow","bzzzzzz","grunt","bawl"};
			final String[] three={"chirp","tweet","mooooo","oink","quack","tweet","bellooooow","cackle","hooooowwwwl","!dook!"};
			final String[] four={"ruff","meow","grrrrowl","roar","cluck","honk","gibber","hoot","snort","groooan","trill","snarl"};
			final String[] five={"croak","bark","blub-blub","cuckoo","squeak","peep","screeech!","twitter","cherp","wail"};
			final String[] six={"hummmmmm","bleat","*whistle*","yelp","neigh","whinny","growl","screeaam!!"};
			final String[] seven={"gobble-gobble","ribbit","b-a-a-a-h","n-a-a-a-y","heehaw","cock-a-doodle-doo"};
			wordLists=new Vector<String[]>();
			wordLists.add(one);
			wordLists.add(two);
			wordLists.add(three);
			wordLists.add(four);
			wordLists.add(five);
			wordLists.add(six);
			wordLists.add(seven);
		}
		return wordLists;
	}
}
