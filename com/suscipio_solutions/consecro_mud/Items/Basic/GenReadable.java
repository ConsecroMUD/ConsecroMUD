package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class GenReadable extends GenItem
{
	@Override public String ID(){	return "GenReadable";}
	public GenReadable()
	{
		super();
		setName("a generic readable thing");
		setDisplayText("a generic readable thing sits here.");
		setDescription("");
		setMaterial(RawMaterial.RESOURCE_WOOD);
		basePhyStats().setSensesMask(PhyStats.SENSE_ITEMREADABLE);
		basePhyStats().setWeight(1);
		recoverPhyStats();
	}


	@Override public boolean isGeneric(){return true;}
	@Override public void recoverPhyStats(){CMLib.flags().setReadable(this,true); super.recoverPhyStats();}
}
