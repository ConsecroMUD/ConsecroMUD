package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Trap_RoomThruPit extends Trap_RoomPit
{
	@Override public String ID() { return "Trap_RoomThruPit"; }
	private final static String localizedName = CMLib.lang().L("small pit trap");
	@Override public String name() { return localizedName; }
	@Override protected int trapLevel(){return 1;}
	@Override public boolean getTravelThroughFlag() { return true; }
}
