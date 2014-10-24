package com.suscipio_solutions.consecro_mud.Exits;


public class StdLockedDoorway extends StdClosedDoorway
{
	@Override public String ID(){	return "StdLockedDoorway";}
	@Override public boolean hasALock(){return true;}
	@Override public boolean defaultsLocked(){return true;}
	@Override public String closedText(){return "a closed, locked door";}
}
