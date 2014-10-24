package com.suscipio_solutions.consecro_mud.Abilities.Languages;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Drunken extends StdLanguage
{
	@Override public String ID() { return "Drunken"; }
	private final static String localizedName = CMLib.lang().L("Drunken");
	@Override public String name() { return localizedName; }
	public static List<String[]> wordLists=null;
	private static boolean mapped=false;
	public Drunken()
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

	protected Vector<Integer> getSChoices(StringBuffer word)
	{
		final Vector<Integer> V=new Vector<Integer>();
		int x=word.toString().toUpperCase().indexOf('S');
		while(x>=0)
		{
			if((x>=word.length()-1)||(Character.toUpperCase(word.charAt(x+1))!='H'))
				V.addElement(Integer.valueOf(x));
			x=word.toString().toUpperCase().indexOf('S',x+1);
		}
		return V;
	}

	protected Vector<Integer> getVChoices(StringBuffer word)
	{
		final Vector<Integer> V=new Vector<Integer>();
		for(int x=0;x<word.length();x++)
		{
			if(("AEIOU").indexOf(Character.toUpperCase(word.charAt(x)))>=0)
			{
				if(V.contains(Integer.valueOf(x-1)))
					V.remove(Integer.valueOf(x-1));
				V.addElement(Integer.valueOf(x));
			}
		}
		return V;
	}

	@Override
	public String translate(String language, String word)
	{
		final StringBuffer sbw=new StringBuffer(word);
		Vector<Integer> V=getSChoices(sbw);
		if(V.size()>0)
			sbw.insert(V.elementAt(CMLib.dice().roll(1,V.size(),-1)).intValue()+1,'h');
		if(CMLib.dice().rollPercentage()<50)
			return fixCase(word,sbw.toString());

		V=getVChoices(sbw);
		if(V.size()>0)
		switch(CMLib.dice().roll(1,3,0))
		{
		case 1:
			{
				final int x=V.elementAt(CMLib.dice().roll(1,V.size(),-1)).intValue();
				for(int i=0;i<CMLib.dice().roll(1,5,0);i++)
					sbw.insert(x+1,sbw.charAt(x));
				break;
			}
		case 2:
			{
				final int x=V.elementAt(CMLib.dice().roll(1,V.size(),-1)).intValue();
				for(int i=0;i<CMLib.dice().roll(1,5,0);i++)
					sbw.insert(x+1,"-"+sbw.charAt(x));
				break;
			}
		case 3:
			{
				final int x=V.elementAt(CMLib.dice().roll(1,V.size(),-1)).intValue();
				sbw.insert(x+1,"sh");
				break;
			}
		}
		return fixCase(word,sbw.toString());
	}

}
