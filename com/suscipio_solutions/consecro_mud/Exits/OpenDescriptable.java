package com.suscipio_solutions.consecro_mud.Exits;


public class OpenDescriptable extends StdExit
{
	@Override public String ID(){	return "OpenDescriptable";}
	@Override public String Name(){ return "the ground";}
	@Override public String displayText(){ return miscText;}
	@Override public String description(){ return miscText;}
}
