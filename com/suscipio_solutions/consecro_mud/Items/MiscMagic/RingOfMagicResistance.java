package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;


public class RingOfMagicResistance extends Ring_Protection implements MiscMagic
{
	@Override public String ID(){	return "RingOfMagicResistance";}
	public RingOfMagicResistance()
	{
		super();
		this.basePhyStats().setLevel(GOLD_RING_OPAL);
		this.recoverPhyStats();
		material=RawMaterial.RESOURCE_GOLD;
	}

}
