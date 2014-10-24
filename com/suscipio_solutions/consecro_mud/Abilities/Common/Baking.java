package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.List;

import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Baking extends Cooking
{
	@Override public String ID() { return "Baking"; }
	private final static String localizedName = CMLib.lang().L("Baking");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"BAKING","BAKE"});
	@Override public String supportedResourceString(){return "MISC";}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public String cookWordShort(){return "bake";}
	@Override public String cookWord(){return "baking";}
	@Override public boolean honorHerbs(){return false;}
	@Override public boolean requireLid(){return true;}

	@Override public String parametersFile(){ return "bake.txt";}
	@Override protected List<List<String>> loadRecipes(){return super.loadRecipes(parametersFile());}
}
