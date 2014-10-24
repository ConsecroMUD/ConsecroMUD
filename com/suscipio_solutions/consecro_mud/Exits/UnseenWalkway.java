package com.suscipio_solutions.consecro_mud.Exits;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;


public class UnseenWalkway extends Open
{
	@Override public String ID(){	return "UnseenWalkway";}
	public UnseenWalkway()
	{
		super();
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_NOT_SEEN);
		recoverPhyStats();
	}
}
