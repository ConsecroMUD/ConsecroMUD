package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;


public class RingOfElectricity extends Ring_Protection implements MiscMagic
{
	@Override public String ID(){	return "RingOfElectricity";}
	public RingOfElectricity()
	{
		super();
		this.basePhyStats().setLevel(COPPER_RING);
		material=RawMaterial.RESOURCE_COPPER;
		this.recoverPhyStats();
	}

}
