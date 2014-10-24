package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Spell_MinManaShield extends Spell_ManaShield
{
	@Override public String ID() { return "Spell_MinManaShield"; }
	private final static String localizedName = CMLib.lang().L("Minor Mana Shield");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Minor Mana Shield)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected double protection(){return 0.25;}
	@Override protected String adjective(){return " a faint";}

}
