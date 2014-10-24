package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Trap_DeepThruPit extends Trap_DeepPit
{
	@Override public String ID() { return "Trap_DeepThruPit"; }
	private final static String localizedName = CMLib.lang().L("small deep pit");
	@Override public String name() { return localizedName; }
	@Override protected int trapLevel(){return 13;}
	@Override public boolean getTravelThroughFlag() { return true; }
}
