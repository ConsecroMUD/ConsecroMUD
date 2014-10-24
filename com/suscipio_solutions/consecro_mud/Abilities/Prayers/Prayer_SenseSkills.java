package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Prayer_SenseSkills extends Prayer_SenseProfessions
{
	@Override public String ID() { return "Prayer_SenseSkills"; }
	private final static String localizedName = CMLib.lang().L("Sense Skills");
	@Override public String name() { return localizedName; }
	@Override protected int senseWhat() { return ACODE_SKILL; }
	@Override protected String senseWhatStr() { return "skills"; }
}
