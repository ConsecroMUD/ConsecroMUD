package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;


public class RingOfJustice extends Ring_Protection implements MiscMagic
{
	@Override public String ID(){	return "RingOfJustice";}
	public RingOfJustice()
	{
		super();
		this.basePhyStats().setLevel(GOLD_RING_SAPPHIRE);
		material=RawMaterial.RESOURCE_GOLD;
		this.recoverPhyStats();
	}

}
