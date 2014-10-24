package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Prayer_SenseSpells extends Prayer_SenseProfessions
{
	@Override public String ID() { return "Prayer_SenseSpells"; }
	private final static String localizedName = CMLib.lang().L("Sense Spells");
	@Override public String name() { return localizedName; }
	@Override protected int senseWhat() { return ACODE_SPELL; }
	@Override protected String senseWhatStr() { return "spells"; }
}
