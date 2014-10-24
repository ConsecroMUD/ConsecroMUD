package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;



public class ScrollSpell extends StdScroll
{
	@Override public String ID(){	return "ScrollSpell";}
	public ScrollSpell()
	{
		super();
		this.setUsesRemaining(2);
		baseGoldValue=200;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_PAPER;
	}
}
