package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Trap_SnakeThruPit extends Trap_SnakePit
{
	@Override public String ID() { return "Trap_SnakeThruPit"; }
	private final static String localizedName = CMLib.lang().L("small snake pit");
	@Override public String name() { return localizedName; }
	@Override protected int trapLevel(){return 9;}
	@Override public boolean getTravelThroughFlag() { return true; }
}
