package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;



@SuppressWarnings("rawtypes")
public class SpellCraftingSkill extends CraftingSkill
{
	@Override public String ID() { return "SpellCraftingSkill"; }
	private final static String localizedName = CMLib.lang().L("Spell Crafting Skill");
	@Override public String name() { return localizedName; }
	public SpellCraftingSkill(){super();}

	protected String getCraftableSpellName(Vector commands)
	{
		String spellName=null;
		if((commands.size()>0)&&(commands.firstElement() instanceof String))
			spellName=CMParms.combine(commands,0);
		else
		{
			final List<List<String>> recipes=loadRecipes();
			final List<String> V=recipes.get(CMLib.dice().roll(1,recipes.size(),-1));
			spellName=V.get(RCP_FINALNAME);
		}
		return spellName;
	}

	protected List<String> getCraftableSpellRow(String spellName)
	{
		List<String> spellFound=null;
		final List<List<String>> recipes=loadRecipes();
		for(final List<String> V : recipes)
			if(V.get(RCP_FINALNAME).equalsIgnoreCase(spellName))
			{ spellFound=V; break;}
		if(spellFound==null)
			for(final List<String> V : recipes)
				if(CMLib.english().containsString(V.get(RCP_FINALNAME),spellName))
				{ spellFound=V; break;}
		if(spellFound==null)
			for(final List<String> V : recipes)
				if(V.get(RCP_FINALNAME).toLowerCase().indexOf(spellName.toLowerCase())>=0)
				{ spellFound=V; break;}
		return spellFound;
	}

	protected Ability getCraftableSpellRecipeSpell(Vector commands)
	{
		Ability theSpell=null;
		final String spellName=getCraftableSpellName(commands);
		if(spellName!=null)
		{
			theSpell=CMClass.getAbility((String)commands.firstElement());
			if(theSpell==null)
			{
				final List<String> spellFound=getCraftableSpellRow(spellName);
				if(spellFound!=null)
					theSpell=CMClass.getAbility(spellFound.get(RCP_FINALNAME));
			}
		}
		return theSpell;
	}

	protected int getCraftableSpellLevel(Vector commands)
	{
		Ability theSpell=null;
		final String spellName=getCraftableSpellName(commands);
		if(spellName!=null)
		{
			final List<String> spellFound=getCraftableSpellRow(spellName);
			if(spellFound!=null)
				return CMath.s_int(spellFound.get(RCP_LEVEL));
			theSpell=CMClass.getAbility((String)commands.firstElement());
			if(theSpell!=null)
				return CMLib.ableMapper().lowestQualifyingLevel(theSpell.ID());
		}
		return -1;
	}
}
