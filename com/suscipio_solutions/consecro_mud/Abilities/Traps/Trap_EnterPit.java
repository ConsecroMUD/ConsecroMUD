package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Trap_EnterPit extends Trap_Enter
{
	@Override public String ID() { return "Trap_EnterPit"; }
	private final static String localizedName = CMLib.lang().L("Entry Pit Trap");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_EXITS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapType(){return TRAP_PIT_BLADE;}
}
