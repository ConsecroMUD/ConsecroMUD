package com.suscipio_solutions.consecro_mud.Abilities.Misc;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Lighteningbreath extends Dragonbreath
{
	@Override public String ID() { return "Lighteningbreath"; }
	private final static String localizedName = CMLib.lang().L("Lightningbreath");
	@Override public String name() { return localizedName; }
	@Override public String text(){return "lightning";}
	private static final String[] triggerStrings =I(new String[] {"LIGHTNINGBREATH"});
	@Override public String[] triggerStrings(){return triggerStrings;}
}
