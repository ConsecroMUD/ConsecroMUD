package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Thief_MinorTrap extends Thief_Trap
{
	@Override public String ID() { return "Thief_MinorTrap"; }
	private final static String localizedName = CMLib.lang().L("Lay Minor Traps");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"MTRAP","MINORTRAP"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}

	@Override protected int maxLevel(){return 3;}
}
