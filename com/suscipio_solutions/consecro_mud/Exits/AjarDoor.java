package com.suscipio_solutions.consecro_mud.Exits;


public class AjarDoor extends StdExit
{
	@Override public String ID(){	return "AjarDoor";}
	@Override public String Name(){ return "a door";}
	@Override public String displayText(){ return "";}
	@Override public String description(){ return "An ordinary wooden door with swinging hinges and a latch.";}
	@Override public boolean hasADoor(){return true;}
	@Override public boolean hasALock(){return false;}
	@Override public boolean defaultsLocked(){return false;}
	@Override public boolean defaultsClosed(){return false;}
	@Override public String closedText(){return "a closed door";}
}
