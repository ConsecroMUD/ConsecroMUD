package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Spell_MajManaShield extends Spell_ManaShield
{
	@Override public String ID() { return "Spell_MajManaShield"; }
	private final static String localizedName = CMLib.lang().L("Major Mana Shield");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Major Mana Shield)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected double protection(){return 0.75;}
	@Override protected String adjective(){return " a powerful";}

}
