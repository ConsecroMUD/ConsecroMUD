package com.suscipio_solutions.consecro_mud.Abilities.Misc;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Frostbreath extends Dragonbreath
{
	@Override public String ID() { return "Frostbreath"; }
	private final static String localizedName = CMLib.lang().L("Frostbreath");
	@Override public String name() { return localizedName; }
	@Override public String text(){return "cold";}
	private static final String[] triggerStrings =I(new String[] {"FROSTBREATH"});
	@Override public String[] triggerStrings(){return triggerStrings;}
}
