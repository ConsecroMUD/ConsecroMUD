package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Trap_OpenGas extends Trap_Open
{
	@Override public String ID() { return "Trap_OpenGas"; }
	private final static String localizedName = CMLib.lang().L("Open Gas Trap");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_EXITS|Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapType(){return TRAP_GAS;}
}
