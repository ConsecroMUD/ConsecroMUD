package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.List;

import com.suscipio_solutions.consecro_mud.core.CMLib;


public class FoodPrep extends Cooking
{
	@Override public String ID() { return "FoodPrep"; }
	private final static String localizedName = CMLib.lang().L("Food Prep");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"FOODPREPPING","FPREP"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public String cookWordShort(){return "make";}
	@Override public String cookWord(){return "making";}
	@Override public boolean honorHerbs(){return false;}
	@Override public boolean requireFire(){return false;}

	@Override public String parametersFile(){ return "foodprep.txt";}
	@Override protected List<List<String>> loadRecipes(){return super.loadRecipes(parametersFile());}

	public FoodPrep()
	{
		super();

		defaultFoodSound = "chopchop.wav";
	}

}
