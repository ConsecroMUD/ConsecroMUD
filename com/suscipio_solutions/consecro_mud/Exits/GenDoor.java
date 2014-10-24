package com.suscipio_solutions.consecro_mud.Exits;


public class GenDoor extends GenExit
{
	@Override public String ID(){	return "GenDoor";}
	public GenDoor()
	{
		super();
		name="a door";
		displayText="";
		description="An ordinary wooden door with hinges and a latch.";
		hasADoor=true;
		hasALock=false;
		doorDefaultsClosed=true;
		doorDefaultsLocked=false;
		closedText="a closed door";
		doorName="door";
		closeName="close";
		openName="open";
	}
}
