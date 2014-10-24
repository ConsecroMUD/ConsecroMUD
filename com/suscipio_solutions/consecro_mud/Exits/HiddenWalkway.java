package com.suscipio_solutions.consecro_mud.Exits;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;


public class HiddenWalkway extends Open
{
	@Override public String ID(){	return "HiddenWalkway";}
	public HiddenWalkway()
	{
		super();
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_HIDDEN);
		recoverPhyStats();
	}
}
