package com.suscipio_solutions.consecro_mud.Exits;


public class GenAirLock extends GenExit
{
	@Override public String ID(){	return "GenAirLock";}
	@Override public String Name(){ return "an air lock";}
	@Override public String displayText(){ return "";}
	@Override public boolean hasADoor(){return true;}
	@Override public boolean hasALock(){return false;}
	@Override public boolean defaultsLocked(){return false;}
	@Override public boolean defaultsClosed(){return true;}
	@Override public String closedText(){return "a closed air lock door";}
	public GenAirLock()
	{
		super();
		name="an air lock door";
		displayText="";
		description="This door leads to the outside of the ship through a small air lock.";
		hasADoor=true;
		hasALock=false;
		doorDefaultsClosed=true;
		doorDefaultsLocked=false;
		closedText="a closed air lock door";
		doorName="door";
		closeName="close";
		openName="open";
	}
}
