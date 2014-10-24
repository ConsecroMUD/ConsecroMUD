package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MiscMagic;




public class PortableHole extends BagOfHolding implements MiscMagic
{
	@Override public String ID(){	return "PortableHole";}
	public PortableHole()
	{
		super();

		setName("a small disk");
		setDisplayText("a small black disk can be found up here.");
		setDescription("It looks like a small disk.");
		secretIdentity="A Portable Hole";
		basePhyStats().setLevel(1);
		capacity=200 * basePhyStats().level();

		baseGoldValue=15000;
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_BONUS);
		recoverPhyStats();



	}
}
