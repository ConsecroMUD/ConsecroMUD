package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Prayer_SensePrayers extends Prayer_SenseProfessions
{
	@Override public String ID() { return "Prayer_SensePrayers"; }
	private final static String localizedName = CMLib.lang().L("Sense Prayers");
	@Override public String name() { return localizedName; }
	@Override protected int senseWhat() { return ACODE_PRAYER; }
	@Override protected String senseWhatStr() { return "prayers"; }
}
