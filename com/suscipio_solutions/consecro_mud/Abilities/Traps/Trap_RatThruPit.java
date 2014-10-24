package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Trap_RatThruPit extends Trap_RatPit
{
	@Override public String ID() { return "Trap_RatThruPit"; }
	private final static String localizedName = CMLib.lang().L("small rat pit");
	@Override public String name() { return localizedName; }
	@Override protected int trapLevel(){return 11;}
	@Override public boolean getTravelThroughFlag() { return true; }
}
