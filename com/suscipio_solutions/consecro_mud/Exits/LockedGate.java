package com.suscipio_solutions.consecro_mud.Exits;





public class LockedGate extends Gate
{
	@Override public String ID(){	return "LockedGate";}
	@Override public String closedText(){return "a closed, locked gate";}
	@Override public boolean hasALock(){return true;}
	@Override public boolean defaultsLocked(){return true;}
}
