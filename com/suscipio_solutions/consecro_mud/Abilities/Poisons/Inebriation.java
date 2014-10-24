package com.suscipio_solutions.consecro_mud.Abilities.Poisons;
import com.suscipio_solutions.consecro_mud.core.CMLib;




public class Inebriation extends Poison_Alcohol
{
	@Override public String ID() { return "Inebriation"; }
	private final static String localizedName = CMLib.lang().L("Inebriation");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"INEBRIATE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int alchoholContribution(){return 6;}
}
