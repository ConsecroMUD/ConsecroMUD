package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Trap_SpikeThruPit extends Trap_SpikePit
{
	@Override public String ID() { return "Trap_SpikeThruPit"; }
	private final static String localizedName = CMLib.lang().L("small spike pit");
	@Override public String name() { return localizedName; }
	@Override protected int trapLevel(){return 7;}
	@Override public boolean getTravelThroughFlag() { return true; }
}
