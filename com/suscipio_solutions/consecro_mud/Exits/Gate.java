package com.suscipio_solutions.consecro_mud.Exits;





public class Gate extends StdClosedDoorway
{
	@Override public String ID(){	return "Gate";}
	@Override public String Name(){return "a gate";}
	@Override public String doorName(){return "gate";}
	@Override public String closedText(){return "a closed gate";}
}
