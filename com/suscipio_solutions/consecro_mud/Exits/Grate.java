package com.suscipio_solutions.consecro_mud.Exits;





public class Grate extends StdClosedDoorway
{
	@Override public String ID(){	return "Grate";}
	@Override public String Name(){return "a barred grate";}
	@Override public String doorName(){return "grate";}
	@Override public String closedText(){return "a closed grate";}
	@Override public String description(){return "A metal grate of thick steel bars is inset here.";}
	@Override public String closeWord(){return "close";}
	@Override public String openWord(){return "remove";}
}
