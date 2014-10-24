package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;


public class RingOfColdProtection extends Ring_Protection implements MiscMagic
{
	@Override public String ID(){	return "RingOfColdProtection";}
	public RingOfColdProtection()
	{
		super();
		this.basePhyStats().setLevel(SILVER_RING);
		material=RawMaterial.RESOURCE_SILVER;
		this.recoverPhyStats();
	}
}
