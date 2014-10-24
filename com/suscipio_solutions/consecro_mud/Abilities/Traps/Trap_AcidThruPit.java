package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Trap_AcidThruPit extends Trap_AcidPit
{
	@Override public String ID() { return "Trap_AcidThruPit"; }
	private final static String localizedName = CMLib.lang().L("small acid pit");
	@Override public String name() { return localizedName; }
	@Override protected int trapLevel(){return 17;}
	@Override public boolean getTravelThroughFlag() { return true; }
}
