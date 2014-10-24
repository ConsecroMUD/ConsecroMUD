package com.suscipio_solutions.consecro_mud.Exits;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;


public class FlyingExit extends StdExit
{
	@Override public String ID(){	return "FlyingExit";}
	@Override public String Name(){ return "the open air";}
	@Override public String displayText(){ return "";}
	@Override public String description(){ return "Looks like you'll have to fly up there.";}
	public FlyingExit()
	{
		super();
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_FLYING);
		recoverPhyStats();
	}
}
