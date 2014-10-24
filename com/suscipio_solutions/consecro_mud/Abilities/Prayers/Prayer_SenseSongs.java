package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Prayer_SenseSongs extends Prayer_SenseProfessions
{
	@Override public String ID() { return "Prayer_SenseSongs"; }
	private final static String localizedName = CMLib.lang().L("Sense Songs");
	@Override public String name() { return localizedName; }
	@Override protected int senseWhat() { return ACODE_SONG; }
	@Override protected String senseWhatStr() { return "songs"; }
}
