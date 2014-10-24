package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class StdBomb extends StdTrap
{
	@Override public String ID() { return "StdBomb"; }
	private final static String localizedName = CMLib.lang().L("a bomb");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public String requiresToSet(){return "";}
	@Override public boolean isABomb(){return true;}
	@Override public int baseRejuvTime(int level){ return 5;}
	public StdBomb(){ super(); reset=3;}

}
