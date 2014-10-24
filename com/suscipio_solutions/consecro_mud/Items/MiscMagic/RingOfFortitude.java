package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;


public class RingOfFortitude extends Ring_Protection implements MiscMagic
{
	@Override public String ID(){	return "RingOfFortitude";}
	public RingOfFortitude()
	{
		super();
		this.basePhyStats().setLevel(MITHRIL_RING);
		material=RawMaterial.RESOURCE_MITHRIL;
		this.recoverPhyStats();
	}

}
