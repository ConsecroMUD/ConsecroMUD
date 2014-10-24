package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;




public class LargeChest extends LockableContainer
{
	@Override public String ID(){	return "LargeChest";}
	public LargeChest()
	{
		super();
		setName("a large chest");
		setDisplayText("a large wooden chest sits here.");
		setDescription("It\\`s of solid wood construction with metal bracings.  The lid has a key hole.");
		capacity=150;
		setMaterial(RawMaterial.RESOURCE_OAK);
		baseGoldValue=50;
		basePhyStats().setWeight(50);
		recoverPhyStats();
	}



}
