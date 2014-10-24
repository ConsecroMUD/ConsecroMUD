package com.suscipio_solutions.consecro_mud.Exits;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;


public class HiddenClosedDoorway extends StdClosedDoorway
{
	@Override public String ID(){	return "HiddenClosedDoorway";}
	@Override public String description(){return "a cleverly concealed door.";}
	public HiddenClosedDoorway()
	{
		super();
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_HIDDEN);
		recoverPhyStats();
	}
}
